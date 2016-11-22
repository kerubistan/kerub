kerubApp.controller('Settings', function($scope, appsession) {
	$scope.config = {};
	$scope.refresh = function() {
		appsession.get('s/r/config').success(function(result) {
			$scope.config = result;
		});
	};

	$scope.save = function() {
		appsession.put('s/r/config',$scope.config).success(function() {
			$scope.refresh();
		});
	}

	$scope.refresh();
});