kerubApp.controller('VirtualStorageLink', function($scope, $uibModal, $log, $http, size) {

	var defaultLink = function() {
		return {
			bus : 'virtio',
			device : 'disk',
			   expectations : [],
			   virtualStorageId : null
		};
	};

	$scope.link = defaultLink();
	$scope.size = size;

	$scope.existingStorageDevice = null;

	$scope.listDisksByName = function(deviceName) {
		$log.info('search', deviceName);
		return $http.get('s/r/virtual-storage/search?field=name&value='+deviceName)
			.then(function(result) {
				$log.debug(result.data.result);
				return result.data.result;
			});
	};
	$scope.linkExistingVirtualDisk = function() {
		$log.info('link storage',$scope.existingStorageDevice);
		$scope.link.virtualStorageId = $scope.existingStorageDevice.id;
		$scope.addStorageLink($scope.link);
		$scope.link = defaultLink();
	};

	$scope.modelOptions = {
        debounce: {
          default: 500,
          blur: 250
        },
        getterSetter: true
    };

});
