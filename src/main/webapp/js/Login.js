var Login = function($scope, $log, $http, $uibModalInstance, appsession) {
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
    $scope.onKeyPress = function(event) {
        if(event.keyCode == 13) {
            $scope.login();
        }
    }
    $scope.login = function() {
        $log.info('Login user ', $scope.username);
        $http.post('s/r/auth/login', {
            username : $scope.username,
            password : $scope.password
        }).success(function() {
            $log.info("success", $uibModalInstance);
            $uibModalInstance.dismiss();
            appsession.restartRequests();
        }).error(function(error){
            $log.info("error",error);
        });
    };
};
