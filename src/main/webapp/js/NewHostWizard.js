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

	$scope.controllerKey = '';

    $scope.updateTimeout = null;
    $scope.clearPublicKey = function () {
		if(event.keyCode == 13) {
			$scope.addHost();
		}
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
	$scope.onKeyPress = function(event) {
		if(event.keyCode == 13) {
			$scope.addHost();
		}
	};
	$scope.toggleDedicated = function() {
		$scope.host.dedicated = !$scope.host.dedicated;
	};
    $scope.addHost = function () {
    	var onHostAdded = function() {
			$log.debug('host add finished');
			$modalInstance.close();
		};
    	if($scope.password === '') {
    	    $log.debug('add host with public key');
			$appsession.put('s/r/host/join-pubkey',
				$scope.host
				).success(onHostAdded);
    	} else {
			$log.debug('add host with password');
			$appsession.put('s/r/host/join',
				{
					host : $scope.host,
					password : $scope.password
				}
				).success(onHostAdded);
    	}
    };

    $appsession.get('s/r/host/helpers/controller-pubkey').success(function(result) {
    	$log.debug('retrieved ssh public key of the controller:'+result);
    	$scope.controllerKey = result;
    });
};