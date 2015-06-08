var HostDetails = function($scope, $log, $modalInstance, $appsession, hostId) {
    $scope.host = null;

    $scope.tab = "overview";

    $appsession
        .get('s/r/host/'+hostId)
        .success(function(host) {
            $log.debug('host details received');
            $scope.host = host;
        });

    $scope.selectView = function(view) {
        $log.info('select view ', view);
        $scope.tab = view;
    };

    $scope.isActive = function(view) {
        if($scope.tab === view) {
            return 'active'
        } else {
            return '';
        }
    }

    $scope.isVisible = function(view) {
        return $scope.tab === view
    }

}
