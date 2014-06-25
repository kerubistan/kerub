var kerubApp = angular.module('kerubApp', []);

kerubApp.controller('Login', function($scope) {
    $scope.login = function($log) {
        $log.info('loging in');
    }
});
