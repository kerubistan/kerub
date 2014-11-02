var kerubApp = angular.module('kerubApp', ['ui.bootstrap', 'ngRoute', 'uuid4', 'ngSanitize']);

kerubApp.controller('Login', function($scope) {
    $scope.login = function($log) {
        $log.info('loging in');
    };
});

