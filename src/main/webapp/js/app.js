var kerubApp = angular.module('kerubApp', ['ngRoute', 'uuid4', 'ngSanitize', 'ui.bootstrap', 'angularFileUpload']);

kerubApp.controller('Login', function($scope) {
    $scope.login = function($log) {
        $log.info('loging in');
    };
});


