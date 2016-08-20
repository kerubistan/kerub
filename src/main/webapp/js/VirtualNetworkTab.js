kerubApp.controller('VirtualNetworkTab', function($scope, $log, $uibModal, socket) {
	$scope.newVirtualNetwork = function() {
	   var modalInstance = $uibModal.open({
			templateUrl : 'NewVirtualNetworkWizard.html',
			controller : NewVirtualNetworkWizard
		});
	};
});
