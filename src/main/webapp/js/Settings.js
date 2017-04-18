kerubApp.controller('Settings', function($scope, appsession, socket, $log) {
	$scope.config = {};
	$scope.outdated = true;
	$scope.newFsPathAdd = false;
	$scope.refresh = function() {
		appsession.get('s/r/config').success(function(result) {
			$scope.config = result;
			$scope.outdated = false;
		});
	};
	socket.subscribe('/config', function(event) {
		$scope.$apply(function() {
			if(!angular.equals($scope.config, event.obj)) {
				$scope.outdated = true;
			}
		});
	});

	$scope.save = function() {
		appsession.put('s/r/config',$scope.config).success(function() {
			$scope.refresh();
		});
	}

	$scope.refresh();
});