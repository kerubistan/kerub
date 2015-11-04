var NewVirtualDiskWizard = function($scope, $modalInstance, $log, appsession, uuid4, size) {
	var id = uuid4.generate();
	$scope.disk = {
		'@type' : 'vstorage',
		id : id,
		name : 'disk-'+id,
		size : 16 * 1024 * 1024 * 1024,
		expectations : []
	};

	$scope.addStorage = function() {
		appsession.put('s/r/virtual-storage', $scope.disk).success(function(result) {
        	$modalInstance.dismiss();
		});
	};

	$scope.close = function() {
		$modalInstance.dismiss();
	};

}
