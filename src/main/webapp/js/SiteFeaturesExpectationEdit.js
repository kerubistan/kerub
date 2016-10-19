kerubApp.controller('SiteFeaturesExpectationEdit', function($scope, $log) {

	$scope.expectation = {
		'@type':'site-features',
		'level': 'Want',
		'features': []
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	}

	$scope.addExpectation = function() {
		$log.info('add storage-redundancy expectation', $scope.expectation)
		$scope.entity.expectations.push($scope.expectation);
	}
});