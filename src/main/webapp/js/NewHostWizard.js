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
    $scope.password = '';

    $scope.updateTimeout = null;
    $scope.updatePubkey = function () {
        $scope.host.publicKey = '';
        if($scope.host.address.length < 6) {
            $log.debug('too short ' + $scope.host.address);
            $scope.pubkeyUptoDate = false;
            return;
        }
        $log.debug('change in hostname: '+$scope.host.address);
        if($scope.updateTimeout != null) {
            $timeout.cancel($scope.updateTimeout);
            $scope.pubkeyUptoDate = false;
        }
        $scope.updateTimeout = $timeout(function() {
            $http.get('s/r/host/helpers/pubkey?address='+$scope.host.address)
                .success(function(pubkey) {
                    $log.debug(pubkey);
                    $scope.pubkeyUptoDate = true;
                    $scope.pubkey = pubkey;
                    $scope.host.publicKey = pubkey.fingerprint;
                });
            }, 2000);
    };
    $scope.close = function() {
        $log.info('close window');
        $modalInstance.dismiss('cancel');
    };
    $scope.addHost = function () {
        $log.debug('add host');
        $http.put('s/r/host/join',
            {
                host : $scope.host,
                password : $scope.password
            }
            );
        $modalInstance.close();
    };
};