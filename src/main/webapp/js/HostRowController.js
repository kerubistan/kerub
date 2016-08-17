kerubApp.controller('HostRowController', function($scope, socket, appsession, $log) {
	$scope.host = {};
	$scope.hostDyn = {};
	$scope.init = function(host) {
		$scope.host = host;
		socket.subscribe('/host-dyn/'+host.id, function(dyn) {
			$scope.$apply(function() {
				$scope.hostDyn = dyn.obj;
			});
		});
	};
});
