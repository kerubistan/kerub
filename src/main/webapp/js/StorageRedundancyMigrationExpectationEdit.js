kerubApp.controller('StorageRedundancyMigrationExpectationEdit', function($scope, $log) {

	$scope.expectation = {
		'@type':'storage-redundancy',
		'level': 'Want',
		'nrOfCopies': 2,
		'outOfBox': false
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	}

	$scope.addExpectation = function() {
		$log.info('add storage-redundancy expectation', $scope.expectation)
		$scope.entity.expectations.push($scope.expectation);
		$scope.closeExpectationForm();
	}
});