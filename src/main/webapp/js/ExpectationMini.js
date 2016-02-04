kerubApp.controller('ExpectationMini', function($scope, $log, size, expectations) {
	$scope.icon = 'cog';
	$scope.description = '';
	$scope.style = 'info';

	var expLevelToStyle = function(expectationLevel) {
		switch(expectationLevel) {
			case 'Hint':
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

	var expToDesc = function(expectation) {
		$log.info('expToDescr');
		var type = expectation['@type'];
		if(expectations.hasOwnProperty(type) && expectations[type].hasOwnProperty('shortDescr')) {
			return expectations[type].shortDescr(expectation);
		} else {
			$log.warn('Unknown expectation type', expectation['@type']);
			return '?';
		}
		$log.info('expToDescr -->');
	};

	$scope.init = function(expectation) {
		$scope.style = expLevelToStyle(expectation.level)
		$scope.icon = expToIcon(expectation);
		$scope.description = expToDesc(expectation);
	}
});