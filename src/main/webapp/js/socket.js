/**
 * Socket API for kerub.
 *
 * The purpose of this module is to bind callbacks to the kerub server's internal
 * message channels with a single websocket. A channel is identified by a path.
 * for example
 *  - /host is for all hosts
 *  - /hosts/e00c5f46-e8b4-4c76-af93-88fca7052200 is for a single host
 *  - /host-dyn is for host dynamic data
 *  - /host-dyn/e00c5f46-e8b4-4c76-af93-88fca7052200 is for dynamic data of a single host
 *
 * Since there can be multiple components subscribing to the same channel the components
 * must identify themselves with the 'clientId'.
 *
 * If a subscription is not yet requested by any of the components, there will be a
 * subscribe message sent to the server.
 *
 */
kerubApp.factory('socket', ['$interval', '$log', 'appsession', function($interval, $log, appsession) {
    //TODO: it would be great to get rid of this sophisticated calculation of the websocket URL
    var socketAddr = (location.protocol === "http:" ? "ws:" : "wss:")
        + '//' + location.hostname
        + ':' + location.port
        + (location.pathname === "/" ? "/" : location.pathname)
        + "ws";
    var sock = {};
    sock.queue = [];
    sock.listeners = {};
    sock.socket = null;
    sock.__onmessage = function(message) {
        var msg = angular.fromJson(message.data);
        var type = msg['@type'];
        if(type === 'entity-update' || type === 'entity-remove' || type === 'entity-add') {
        	var entityId = msg.obj.id;
        	var entityType = msg.obj['@type'];
			angular.forEach(sock.listeners, function(callbacks, channel) {
				if(channel == '/' + entityType
					|| channel == '/' + entityType + '/'
					|| channel == '/' + entityType + '/' + entityId
					|| channel == '/' + entityType + '/' + entityId + '/') {

					angular.forEach(callbacks, function(callback, clientId) {
						callback(msg);
					});

				}
			});
        }
    };
    sock.start = function() {
		$log.debug('socket start');
		//TODO
		var socket = new WebSocket(socketAddr);
		sock.socket = socket;
		socket.onmessage = sock.__onmessage;
		socket.onopen = function() {
			$log.info('connection established');
			for(i = 0; i < sock.queue.length; i++) {
				$log.debug('delayed sending msg', sock.queue[i]);
				sock.socket.send(angular.toJson(sock.queue[i]));
			}
			$log.debug('all msg sent');
			sock.queue = [];
		};
		socket.onclose = function() {
			socket._onclose();
		};
    };
    sock._onclose = function() {
		$log.debug('socket closed');
		appsession.get('s/r/auth/user', function() {
			sock.start();
		});
    };
    sock.send = function(msg) {
        if(sock.socket != null && sock.socket.readyState === WebSocket.OPEN) {
            sock.socket.send(angular.toJson(msg));
        } else {
            $log.debug("not connected, send delayed", msg);
            sock.queue.push(msg);
        }
    };
    sock.subscribe = function(channel, callback, clientId) {
        var msg = {
            "@type" : 'subscribe',
            channel : channel
        };
        try {

            if(!sock.listeners[channel]) {
                $log.debug('sending subscribe to channel '+channel);
                sock.send(msg);
                sock.listeners[channel] = {};
            }
            sock.listeners[channel][clientId] = callback;

        } catch (e) {
            $log.debug('subscribe was not sent', e);
        }
    };
    sock.unsubscribe = function(channel, clientId) {
        if(sock.listeners[channel]) {
            //remove callback
            delete sock.listeners[channel][clientId];
            //if it was the last callback, remove the channel as well
            if(sock.listeners[channel].length === 0) {
                var msg = {
                    "@type" : 'unsubscribe',
                    channel : 'channel'
                };
                sock.send(msg);
                $log.info('unsubscribe sent from channel ' + channel);
                delete sock.listeners[channel];
            }
        }
    };
    sock.start();
    $interval(function() {
        sock.send({ '@type' : 'ping' });
    }, 60000);
    return sock;
}]);
