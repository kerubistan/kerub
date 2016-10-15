kerubApp.controller('NotSameHostExpectationEdit', function($scope, $log) {

	$scope.expectation = {
		'@type':'not-same-host',
		'level': 'Wish',
		'otherVmIds': []
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	}

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
	}
});