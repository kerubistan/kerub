var kerubApp = angular.module('kerubApp', ['ngRoute', 'uuid4', 'ngSanitize', 'ui.bootstrap']);

kerubApp.controller('Login', function($scope) {
    $scope.login = function($log) {
        $log.info('loging in');
    };
});

