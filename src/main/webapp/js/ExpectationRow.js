kerubApp.controller('ExpectationRow', function($scope, expectations) {

	$scope.icon = 'cog';
	$scope.description = '';
	$scope.style = 'info';

	$scope.init = function(expectation) {
		$scope.style = expLevelToStyle(expectation.level)
		$scope.icon = expToIcon(expectation);
		$scope.description = expToDesc(expectation);
	};

});