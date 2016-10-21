kerubApp.controller('NotSameHostExpectationEdit', function($scope, $log, $http) {

	$scope.expectation = {
		'@type':'not-same-host',
		'level': 'Wish',
		'otherVmId': null
	};

	$scope.otherVm = null;

	$scope.init = function(entity) {
		$scope.entity = entity;
	};

	$scope.listVms = function(prefix) {
		return $http.get('s/r/vm/search?field=name&value='+prefix).then(function(result) {
			return result.data.result;
		});
	};

	$scope.selectVm = function($item, $model, $label) {
		$scope.expectation.otherVmId = $scope.otherVm.id;
	};

	$scope.addExpectation = function() {
		$scope.entity.expectations.push($scope.expectation);
		$scope.closeExpectationForm();
	}

	$scope.modelOptions = {
		debounce: {
			default: 500,
			blur: 250
		},
		getterSetter: true
	}

});