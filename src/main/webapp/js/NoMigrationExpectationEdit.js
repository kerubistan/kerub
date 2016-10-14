kerubApp.controller('NoMigrationExpectationEdit', function($scope, $log) {

	$scope.expectation = {
		'@type':'no-migration',
		'level': 'Wish',
		'userTimeoutMinutes': 120
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	}

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
	}
});