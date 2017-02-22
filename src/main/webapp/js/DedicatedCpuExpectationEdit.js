kerubApp.controller('DedicatedCpuExpectationEdit', function($scope, $log, $http, size) {

	$scope.expectation = {
		'@type':'cpu-dedication',
		'level': 'Want'
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	};

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
		$scope.closeExpectationForm();
	}

});