var NewHostWizard = function($scope, $uibModalInstance, $http, $log, $timeout, appsession, uuid4) {
    $scope.pubkeyUptoDate = false;
    $scope.pubkeyUpdating = false;
    $scope.inprg = false;

    $scope.errors = [];

    $scope.host = {
        "@type" : 'host',
        id : uuid4.generate(),
        address : '',
        publicKey : '',
        dedicated : true
    };
    $scope.password = {
    	password: ''
    };

	$scope.usepubkey = true;

	$scope.controllerKey = '';

    $scope.updateTimeout = null;
    $scope.clearPublicKey = function () {
		if(event.keyCode == 13) {
			$scope.addHost();
		}
        $scope.host.publicKey = '';
    }
    $scope.errorHandler = function(error, responseCode) {
		$scope.errors = [error];
    };
    $scope.updatePubkey = function () {
    	if($scope.host.address == '') {
    		return;
    	}
        $scope.host.publicKey = '';
        $scope.pubkeyUpdating = true;
        $log.debug('change in hostname: '+$scope.host.address);
        if($scope.updateTimeout != null) {
            $timeout.cancel($scope.updateTimeout);
            $scope.pubkeyUptoDate = false;
        }
        appsession.get('s/r/host/helpers/pubkey?address='+$scope.host.address)
            .success(function(pubkey) {
                $log.debug(pubkey);
                $scope.pubkeyUptoDate = true;
                $scope.pubkey = pubkey;
                $scope.host.publicKey = pubkey.fingerprint;
		        $scope.pubkeyUpdating = false;
		        scope.errors = [];
            })
            .error(function() {
            	$scope.errorHandler();
            	$scope.pubkeyUpdating = false;
            });
    };
    $scope.close = function() {
        $log.info('close window');
        $uibModalInstance.dismiss('cancel');
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
			$uibModalInstance.close();
		};
		var hostAddError = function(error) {
			$log.error('Host add failed', error);
		};
		$log.info('password',$scope.password);
		$log.info('host',$scope.host);
		$scope.inprg = true;
    	if($scope.password.password === '') {
    	    $log.debug('add host with public key');
			appsession.put('s/r/host/join-pubkey',$scope.host)
				.success(onHostAdded)
				.error($scope.errorHandler);
    	} else {
			$log.debug('add host with password');
			appsession.put('s/r/host/join',
				{
					host : $scope.host,
					password : $scope.password.password
				})
				.success(onHostAdded)
				.error($scope.errorHandler);
    	}
    };

    appsession.get('s/r/host/helpers/controller-pubkey').success(function(result) {
    	$log.debug('retrieved ssh public key of the controller:'+result);
    	$scope.controllerKey = result;
    });
};