
kerubApp.controller('VirtualDisksTab', function($scope, $modal, $log, $socket, $appsession, $size) {
	$scope.humanFriendlySize = $size.humanFriendlySize;
	$scope.virtualDisks = {};
	$scope.refresh = function () {
		$appsession.get('s/r/virtual-storage').success(function(result) {
			$scope.virtualDisks = result;
		});
	};

	$scope.refresh();
});
