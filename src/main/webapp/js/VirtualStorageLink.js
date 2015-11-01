kerubApp.controller('VirtualStorageLink', function($scope, $modal, $log, $http) {
	$scope.link = {
		bus : 'virtio',
        expectations : [],
        virtualStorageId : null
	};
	$scope.existingStorageLink = '';
	$scope.storageDeviceName = function(deviceId) {
		return 'placeholder';
	};
	$scope.storageDeviceSize = function(deviceId) {
		return 'placeholder';
	};
	$scope.selectExisting = function() {
		$log.info('selected');
	};
	$scope.listDisksByName = function(deviceName) {
		$log.info('search', deviceName);
		return $http.get('s/r/virtual-storage/search?field=name&value='+deviceName)
			.success(function(result) {
				$log.debug(result);
				return result.result;
			});
	};
});