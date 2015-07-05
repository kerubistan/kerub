var NewHostWizard = function($scope, $modalInstance, $http, $log, $timeout, $appsession, uuid4) {
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
    $scope.clearPublicKey = function () {
        $scope.host.publicKey = '';
    }
    $scope.updatePubkey = function () {
        $scope.host.publicKey = '';
        $log.debug('change in hostname: '+$scope.host.address);
        if($scope.updateTimeout != null) {
            $timeout.cancel($scope.updateTimeout);
            $scope.pubkeyUptoDate = false;
        }
        $appsession.get('s/r/host/helpers/pubkey?address='+$scope.host.address)
            .success(function(pubkey) {
                $log.debug(pubkey);
                $scope.pubkeyUptoDate = true;
                $scope.pubkey = pubkey;
                $scope.host.publicKey = pubkey.fingerprint;
            });
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