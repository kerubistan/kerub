
kerubApp.controller('HostTab', function($scope, $modal, $log, $socket, $appsession) {

    $log.info('initializing host tab');

    $socket.subscribe('/host', function(msg) {
        $log.info("hey, a message for HostTab!",msg);
    }, 'HostTab');

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
    $appsession.get('s/r/host').success(function(hostsResult) {
        $log.info("hosts", hostsResult);
        $scope.hosts = hostsResult.result;
    });
});
