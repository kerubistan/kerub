kerubApp.controller('StorageDeviceRow', function($scope, $log, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;

    $scope.init = function(id) {
		appsession.get('s/r/virtual-storage/'+id).then(function(data) {
			$scope.storageDevice = data;
			$scope.friendlyDiskSize = $scope.humanFriendlySize($scope.storageDevice.size);
		});
    };
});
