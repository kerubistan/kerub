/**
 * This service makes sure that the rest interactions are executed in a
 * session, enforces user authentication, etc.
 */
kerubApp.factory('appsession', ['$log', '$http', '$uibModal', function($log, $http, $uibModal) {
    $log.info('creating instance of appsession');

	var nr = 0;

    /**
     * The wrapper class to wrap requests.
     * When authentication is finished
     */
    var SessionReqWrapper = function(method, req, session, url, options) {
        this.onSuccess = [];
        this._session = session;
        this.onError = [];
        this.nr = nr++;
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
            this.onSuccess.push(callback);
            return this;
        };
        this.error = function(callback) {
            this.onError.push(callback);
            return this;
        };
        this.then = function(callback) {
            this.onError.push(callback);
            this.onSuccess.push(callback);
            return this;
        };
        this.runSuccessCallbacks = function(response) {
            for(var idx = 0; idx < this.onSuccess.length; idx++) {
                $log.debug('calling onsuccess method', idx);
                this.onSuccess[idx](response);
            }
        }
        req.success(function(response) {
            this.runSuccessCallbacks(response);
        }.bind(this));
        req.error(function(error, responseCode) {
            $log.info('error', req, error, responseCode);
            if(responseCode === 401 && error.code === "AUTH1") {
                this._session._openLogin();
                this._session._blockedRequests.push(this);
            } else {
            	for(var idx = 0; idx < this.onError.length; idx++) {
            		this.onError[idx](error, responseCode);
            	}
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
        	var url = bReq.getUrl();
        	$log.debug('Sending ' + method + ' url: ' + url)
            switch(method) {
                case "GET":
                    return $http.get(url);
                case "POST":
                    return $http.post(url, bReq.getData());
                case "PUT":
                    return $http.put(url, bReq.getData());
                case "DELETE":
                    return $http.delete(bReq.getUrl());
            }
            throw "Unhandled method: " + bReq.method;
        },
        restartRequest : function(bReq) {
                $log.info('restart request',bReq.getUrl());
                var resp = session._sendNewRequest(bReq);
                resp.success(function(result) {
                    $log.info('result', bReq.nr, bReq.getUrl());
                    bReq.runSuccessCallbacks(result);
                });
                resp.error(function() {
                    //hold!
                });
        },
        restartRequests : function() {
            $log.info("restarting requests", session);
            for(var i = 0; i < session._blockedRequests.length; i++) {
            	this.restartRequest(this._blockedRequests[i]);
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
        /**
         * session ost
         */
        post : function(url, data) {
            var res = $http.post(url, data);
            var wrap = new SessionReqWrapper('PUT', res, this);
            return wrap;
        },
        _openLogin : function() {
        	if(session.loginWindow != null) {
        		$log.debug("login window already open");
        		return;
        	}
            $log.info("opening login window");
            session.loginWindow = $uibModal.open({
                templateUrl : 'Login.html',
                controller : Login,
                keyboard : false,
                backdrop : false
                });
        }
    };
    return session;
}]);
