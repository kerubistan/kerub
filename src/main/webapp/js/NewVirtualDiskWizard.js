var NewVirtualDiskWizard = function($scope, $modalInstance, $log, appsession, uuid4, size) {
	var id = uuid4.generate();

	$scope.disk = {
		'@type' : 'vstorage',
		id : id,
		name : 'disk-'+id,
		size : size.toSize('16 GB'),
		userFriendlySize : function(newSize) {
			$log.debug(newSize);
			if(newSize) {
				$scope.disk.size = size.toSize(newSize);
			}
			return size.humanFriendlySize($scope.disk.size);
		},
		expectations : []
	};

	/**
	 * gettersetter for disk size
	 */
	$scope.diskSize = function(val) {
		if(val) {
			$log.info(val);
		} else {
		}
	}

	$scope.addStorage = function() {
		appsession.put('s/r/virtual-storage', $scope.disk).success(function(result) {
        	$modalInstance.dismiss();
		});
	};

	$scope.close = function() {
		$modalInstance.dismiss();
	};

}
