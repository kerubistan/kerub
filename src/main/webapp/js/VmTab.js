
kerubApp.controller('VmTab', function($scope, $modal, $log, $socket, $appsession) {
	$scope.newVmForm = function () {
		var modalInstance = $modal.open({
		   templateUrl : 'NewVmWizard.html',
		   controller : NewVmWizard
        });
		modalInstance.result.then(function() {
			$log.info('kakukk');
		});

	}
});