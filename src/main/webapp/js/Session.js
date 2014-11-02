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
    var SessionReqWrapper = function(req, session) {
        this.onSuccess = [];
        this._session = session;
        this.onError = [];
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
            switch(bReq.method) {
                case "GET":
                    return $http.get(bReq.url);
                case "POST":
                    return $http.post(bReq.url, bReq.data);
                case "PUT":
                    return $http.put(bReq.url, bReq.data);
                case "DELETE":
                    return $http.delete(bReq.url);
            }
            throw "Unhandled method: " + bReq.method;
        },
        restartRequests : function() {
            $log.info("restarting requests - close window");
            session.loginWindow.close();
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
        get : function(url) {
            var res = $http.get(url);
//            res.error(function(error) {
//                $log.info(error);
//                $log.info(error.code);
//                if(error.code === "AUTH1") {
//                    //add the request to the lis of blocked requests
//                    session._blockedRequests.push({
//                        method : 'GET',
//                        url : url,
//                        future : res
//                    });
//
//                    $log.info("opening login window");
//                    console.log(session);
//                    session.loginWindow = $modal.open({
//                        templateUrl : 'Login.html',
//                        controller : Login,
//                        keyboard : false,
//                        backdrop : false
//                        });
//                    $log.info("user can login now");
//                }
//            });
            var wrap = new SessionReqWrapper(res, this);
            return wrap;
        },
        _openLogin : function() {
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
