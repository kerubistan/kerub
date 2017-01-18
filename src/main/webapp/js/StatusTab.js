kerubApp.controller('StatusTab', function($scope, $log, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;
	$scope.basicBalanceReport = {};

	$scope.refreshing = false;

	$scope.refresh = function() {
		$scope.refreshing = true;
		appsession.get('s/r/stats/usage/balance').then(function(result) {
			$scope.basicBalanceReport = result;
			$scope.refreshing = false;
		});
	};
});