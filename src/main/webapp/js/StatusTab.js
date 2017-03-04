kerubApp.controller('StatusTab', function($scope, $log, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;
	$scope.basicBalanceReport = {};

	$scope.refreshing = false;

	$scope.refreshingCaches = false;
	$scope.caches = [];

	$scope.refresh = function() {
		$scope.refreshBalanceReport();
		$scope.refreshCacheUsage();
	};

	$scope.refreshBalanceReport = function() {
		$scope.refreshing = true;
		appsession.get('s/r/stats/usage/balance').then(function(result) {
			$scope.basicBalanceReport = result;
			$scope.refreshing = false;
		});
	};

	$scope.refreshCacheUsage = function() {
		$scope.caches = [];
		$scope.refreshingCaches = true;
		appsession.get('s/r/stats/controller/db').then(function(result) {
			$log.info('result', result);
			$scope.refreshingCaches = false;
			$scope.caches = result;
		});
	};
});