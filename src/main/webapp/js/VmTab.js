
kerubApp.controller('VmTab', function($scope, $modal, $log, socket, $appsession, size) {
	$scope.humanFriendlySize = size.humanFriendlySize;
	$scope.vms = [];
	$scope.refreshVms = function() {
		$appsession.get('s/r/vm').success(function(result) {
			$scope.vms = result.result;
		});
	};
	$scope.refreshVms();
	$scope.newVmForm = function () {
		var modalInstance = $modal.open({
		   templateUrl : 'NewVmWizard.html',
		   controller : NewVmWizard
        });
		modalInstance.result.then(function() {
		});

	}
});