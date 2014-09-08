
kerubApp.controller('HostTab', function($scope, $http, $modal, $log, $socket) {

    $log.info('initializing host tab');

    $socket.subscribe('/host', function(msg) {
        $log.info("hey, a message!",msg);
    });


    $scope.newHostForm = function() {
        $log.info('opening new host wizard');
        var modalInstance = $modal.open({
            templateUrl : 'NewHostWizard.html',
            controller : NewHostWizard
        });
        modalInstance.result.then(function() {
            $log.info('kakukk');
        });
        $log.debug(modalInstance);
        $log.info('opened new host wizard');
    };
    $http.get('s/r/host').success(function(hosts) {
        $scope.hosts = hosts;
    });
});
