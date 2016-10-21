kerubApp.controller('CpuClockFreqExpectationEdit', function($scope, $log, $http) {

	$scope.expectation = {
		'@type':'cpu-clock-freq',
		'level': 'Wish',
		'minimalClockFrequency': 1000
	};

	$scope.init = function(entity) {
		$scope.entity = entity;
	};

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
		$scope.closeExpectationForm();
	}

});