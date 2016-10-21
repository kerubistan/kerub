kerubApp.controller('CacheSizeExpectationEdit', function($scope, $log, $http, size) {

	$scope.expectation = {
		'@type':'cache-size',
		'level': 'Want',
		'minL1': '1 MB'
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	};

	$scope.addExpectation = function() {
		$scope.expectation.minL1 = size.toSize($scope.expectation.minL1);
		$scope.entity.expectations.push($scope.expectation);
	}

});