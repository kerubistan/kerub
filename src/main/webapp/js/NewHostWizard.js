var NewHostWizard = function($scope, $modalInstance, $http, $log, $timeout, uuid4) {
    $scope.pubkeyUptoDate = false;
    $scope.pubkeyUpdating = false;
    $scope.host = {
        "@type" : 'host',
        id : uuid4.generate(),
        address : '',
        publicKey : '',
        dedicated : true
    };
    $scope.updateTimeout = null;
    $scope.updatePubkey = function () {
        if($scope.host.address.length < 6) {
            $log.debug('too short ' + $scope.host.address);
            return;
        }
        $log.debug('change in hostname: '+$scope.host.address);
        if($scope.updateTimeout != null) {
            $timeout.cancel($scope.updateTimeout);
        }
        $scope.updateTimeout = $timeout(function() {
            $http.get('s/r/host/helpers/pubkey?address='+$scope.host.address)
                .success(function(pubkey) {
                    $scope.pubkey = pubkey;
                });
            }, 2000);
    };
    $scope.close = function() {
        $log.info('close window');
        $modalInstance.dismiss('cancel');
    };
    $scope.addHost = function () {
        $log.debug('add host');
        $http.put('s/r/host/', $scope.host);
        $modalInstance.close();
    };
};