
kerubApp.factory('$socket', ['$interval', '$log', function($interval, $log) {
    $log.info("init socket");
    var socketAddr = (location.protocol == 'http:' ? 'ws:' : 'wss:')
        + '//' + location.hostname
        + ':' + location.port
        + "/ws";
    $log.info("socket addr:"+socketAddr);
    var sock = {};
    var socket = new WebSocket(socketAddr, 'kerub');
    sock.socket = socket;
    sock.queue = [];
    socket.onopen = function() {
        $log.info('connection established');
        for(i = 0; i < sock.queue.length; i++) {
            $log.debug('delayed sending msg', sock.queue[i]);
            sock.socket.send(angular.toJson(sock.queue[i]));
        }
        $log.debug('all msg sent');
        sock.queue = [];
    };
    sock.send = function(msg) {
        if(sock.socket.readyState == WebSocket.OPEN) {
            sock.socket.send(angular.toJson(msg));
        } else {
            $log.info("not connected, send delayed", msg);
            sock.queue.push(msg);
        }
    }
    sock.subscribe = function(channel, callback) {
        var msg = {
            "@type" : 'subscribe',
            channel : channel
        };
        try {
            $log.debug('sending subscribe');
            sock.send(msg);
            $log.debug('subscribe sent');
        } catch (e) {
            $log.debug('subscribe was not sent', e);
        }
    };
    sock.unsubscribe = function(channel) {
        var msg = {
            "@type" : 'unsubscribe',
            channel : 'channel'
        };
        socket.send(msg);
    };
    $interval(function() {
        $log.debug('ping server');
        sock.send({ type : 'ping' , msg : {} });
    }, 60000);
    return sock;
}]);
