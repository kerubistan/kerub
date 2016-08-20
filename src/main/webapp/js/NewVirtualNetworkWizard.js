var NewVirtualNetworkWizard = function($scope, $uibModalInstance, $log, appsession, uuid4) {
	$scope.inprg = false;
	$scope.vnet = {
		'@type': 'vnet',
		id : uuid4.generate(),
		name : '',
		expectations : []
	};

	$scope.addVirtualNetwork = function() {
		$scope.inprg = true;
		appsession.put('/s/r/vnet', $scope.vnet).then(function() {
			$scope.inprg = false;
			$uibModalInstance.dismiss();
		});
	};

	$scope.close = function() {
		$uibModalInstance.dismiss();
	};
}