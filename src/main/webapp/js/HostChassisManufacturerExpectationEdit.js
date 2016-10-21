kerubApp.controller('HostChassisManufacturerExpectationEdit', function($scope, $log, $http) {

	$scope.expectation = {
		'@type':'host-chassis-manufacturer',
		'level': 'Wish',
		'manufacturer': null
	};

	$scope.manufacturer = null;

	$scope.init = function(entity) {
		$scope.entity = entity;
	};

	$scope.listManufacturers = function() {
		//TODO retrieve this from the actual hosts, not hardcoded list
		return ["IBM","DELL","INTEL","LENOVO", "HP", "FUJITSU", "ORACLE", "QEMU"];
	};

	$scope.selectManufacturer = function() {
		$scope.expectation.manufacturer = $scope.manufacturer;
	};

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
		$scope.closeExpectationForm();
	}

});