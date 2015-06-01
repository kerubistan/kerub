var HostDetails = function($scope, $log, $modalInstance, $appsession, hostId) {
    $scope.host = null;

    $appsession
        .get('s/r/host/'+hostId)
        .success(function(host) {
            $log.debug('host details received');
            $scope.host = host;
        })
}
