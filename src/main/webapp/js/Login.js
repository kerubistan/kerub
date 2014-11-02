var Login = function($scope, $log, $http, $modal, $appsession) {
    $scope.username = '';
    $scope.password = '';
    $scope.version = {};
    $scope.motd = '';

    $http.get('s/r/meta/version').success(function(version) {
        $scope.version = version;
    });
    $http.get('s/r/motd').success(function(motdMarkDown) {
        $scope.motd = markdown.toHTML(motdMarkDown);
    });
    $scope.login = function() {
        $log.info('Login user ', $scope.username);
        $http.post('s/r/auth/login', {
            username : $scope.username,
            password : $scope.password
        }).success(function(response) {
            $log.info("success", response, $modal);
            $appsession.restartRequests();
        }).error(function(error){
            $log.info("error",error);
        });
    };
};
