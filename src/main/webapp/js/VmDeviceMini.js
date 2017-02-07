kerubApp.controller('VmDeviceMini', function($scope) {

	$scope.device = null;
	$scope.devices = [];

	$scope.init = function(device, devices) {
		$scope.device = device;
		$scope.devices = devices;
	};

	$scope.remove = function() {
		$scope.devices.splice( $scope.devices.indexOf($scope.device), 1 );
	};
});