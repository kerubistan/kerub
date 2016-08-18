kerubApp.controller('HostRowController', function($scope, socket, appsession, $log) {
	$scope.host = {};
	$scope.hostDyn = null;
	$scope.init = function(host) {
		$scope.host = host;
		socket.subscribe('/host-dyn/'+host.id, function(upd) {
			$scope.$apply(function() {
				if(upd['@type'] == 'entity-update') {
					$scope.hostDyn = upd.obj;
				} else {
					$scope.hostDyn = null;
				}
			});
		});
	};
});
