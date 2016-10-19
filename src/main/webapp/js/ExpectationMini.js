kerubApp.controller('ExpectationMini', function($scope, $log, size, expectations) {
	$scope.icon = 'cog';
	$scope.description = '';
	$scope.style = 'info';

	var expLevelToStyle = function(expectationLevel) {
		switch(expectationLevel) {
			case 'Wish':
				return 'success';
			case 'Want':
				return 'info';
			case 'DealBreaker':
				return 'warning';
			default:
				$log.error('Unknown expectation level', expectationLevel);
				return 'error';
		}
	};

	var expToIcon = function(expectation) {
		var type = expectation['@type'];
		if(expectations.hasOwnProperty(type) && expectations[type].hasOwnProperty('icon')) {
			return expectations[type].icon;
		} else {
			$log.warn('Unknown expectation type', expectation['@type']);
			return 'cog';
		}
	};

	$scope.init = function(expectation) {
		$scope.style = expLevelToStyle(expectation.level)
		$scope.icon = expToIcon(expectation);
		var type = expectation['@type'];
		if(expectations.hasOwnProperty(type) && expectations[type].hasOwnProperty('shortDescr')) {
			var descr = expectations[type].shortDescr(expectation);
			if(typeof descr == 'string') {
				$scope.description = descr;
			} else {
				descr.then(function(result){
					$scope.description = result;
				});
			}
		} else {
			$log.warn('Unknown expectation type', expectation['@type']);
			$scope.description = '?';
		}
	}
});