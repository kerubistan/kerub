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

	$scope.removeFsPathEnabled = function(path) {
		var storageCfg = $scope.config.storageTechnologies;
		var idx = storageCfg.fsPathEnabled.indexOf(path)
		var len = storageCfg.fsPathEnabled.length;
		storageCfg.fsPathEnabled
			= storageCfg.fsPathEnabled.slice(0, idx).concat(
					storageCfg.fsPathEnabled.slice(idx + 1, len)
				)
	}

	$scope.save = function() {
		appsession.put('s/r/config',$scope.config).success(function() {
			$scope.refresh();
		});
	}

	$scope.refresh();
});