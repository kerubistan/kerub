/**
 * This service makes sure that the rest interactions are executed in a
 * session, enforces user authentication, etc.
 */
kerubApp.factory('$appsession', ['$log', '$http', '$modal', function($log, $http, $modal) {
    $log.info('creating instance of $appsession');

    /**
     * The wrapper class to wrap requests.
     * When authentication is finished
     */
    var SessionReqWrapper = function(method, req, session, url, options) {
        this.onSuccess = [];
        this._session = session;
        this.onError = [];
        this.getMethod = function() {
			return method;
        };
        this.getUrl = function() {
        	return url;
        };
        this.getOptions = function() {
        	return options;
        };
        this.success = function(callback) {
            $log.debug('onsuccess:', callback);
            this.onSuccess.push(callback);
            return this;
        };
        this.error = function(callback) {
            $log.debug('onerror:', callback)
            this.onError.push(callback);
            return this;
        };
        req.success(function(response) {
            $log.info('success', req, response, this.onSuccess);
            for(idx in this.onSuccess) {
                $log.debug('calling onsuccess method');
                this.onSuccess[idx](response)
            }
        }.bind(this));
        req.error(function(error, responseCode) {
            $log.info('error', req, error, responseCode);
            if(responseCode === 401 && error.code === "AUTH1") {
                this._session._openLogin();
                this._session._blockedRequests.push(this);
            }
        }.bind(this));
    };

    /**
     * Session is a wrapper around the $http service,
     * it adds an extra listener to the Unauthorized (401) responses
     */
    var session = {
        loginWindow : null,
        _blockedRequests : [],

        _sendNewRequest : function(bReq) {
        	var method = bReq.getMethod();
        	$log.debug('Sending ' + method)
            switch(method) {
                case "GET":
                    return $http.get(bReq.getUrl());
                case "POST":
                    return $http.post(bReq.getUrl(), bReq.getData());
                case "PUT":
                    return $http.put(bReq.getUrl(), bReq.getData());
                case "DELETE":
                    return $http.delete(bReq.getUrl());
            }
            throw "Unhandled method: " + bReq.method;
        },
        restartRequests : function() {
            $log.info("restarting requests - close window");
            $log.info("restarting requests", session);
            for(var i = 0; i < session._blockedRequests.length; i++) {
                var bReq = session._blockedRequests[i];
                $log.info('restart request',bReq);
                var resp = session._sendNewRequest(bReq);
                resp.success(function(result) {
                    $log.info('result', bReq,result);
                });
                resp.error(function() {
                    //hold!
                });
            }

            session._blockedRequests = [];
        },
        /**
         * session get
         */
        get : function(url, options) {
            var res = $http.get(url, options);
            var wrap = new SessionReqWrapper('GET', res, this, url, options);
            return wrap;
        },
        /**
         * session put
         */
        put : function(url, data) {
            var res = $http.put(url, data);
            var wrap = new SessionReqWrapper('PUT', res, this);
            return wrap;
        },
        _openLogin : function() {
        	if(session.loginWindow != null) {
        		$log.debug("login window already open");
        		return;
        	}
            $log.info("opening login window");
            session.loginWindow = $modal.open({
                templateUrl : 'Login.html',
                controller : Login,
                keyboard : false,
                backdrop : false
                });
        }
    };
    return session;
}]);
