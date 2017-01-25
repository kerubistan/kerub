var NewVirtualNetworkWizard = function($scope, $uibModalInstance, $log, appsession, uuid4) {
	$scope.inprg = false;
	$scope.vnet = {
		'@type': 'vnet',
		id : uuid4.generate(),
		name : '',
		expectations : []
	};

	$scope.validating = false;
	$scope.valid = true;

	$scope.addVirtualNetwork = function() {
		$scope.inprg = true;
		appsession.put('/s/r/vnet', $scope.vnet).then(function() {
			$scope.inprg = false;
			$uibModalInstance.dismiss();
		});
	};

	$scope.checkVirtualNetworkName = function() {
		var name = $scope.vnet.name;
		if(name.length > 0) {
			appsession.get('s/r/vnet/byname/' + name).success(function(results) {
				if(name == $scope.vnet.name) {
					$scope.valid = results.length == 0;
				}
			});
		} else {
			$scope.valid = false;
		}
	};

	$scope.close = function() {
		$uibModalInstance.dismiss();
	};
}