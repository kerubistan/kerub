kerubApp.controller('CacheStatistics', function($scope, appsession) {
	$scope.cache = '';
	$scope.stat = {};
	$scope.refresh = function() {
		appsession.get('s/r/stats/controller/db/cache/'+$scope.cache).then(function(result) {
			$scope.stat = result;
		});
	};
	$scope.init = function(cache) {
		$scope.cache = cache;
		$scope.refresh();
	};
});