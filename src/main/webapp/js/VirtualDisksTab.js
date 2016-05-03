
kerubApp.controller('VirtualDisksTab', function($scope, $uibModal, $log, socket, appsession, size) {
	$scope.humanFriendlySize = size.humanFriendlySize;
	$scope.virtualDisks = {};
	$scope.refresh = function () {
		appsession.get('s/r/virtual-storage').success(function(result) {
			$scope.virtualDisks = result;
		});
	};

	$scope.newVirtualDisk = function() {
               var modalInstance = $uibModal.open({
                    templateUrl : 'NewVirtualDiskWizard.html',
                    controller : NewVirtualDiskWizard
                });
	};

	socket.subscribe('/virtual-storage/', function(msg) {
		$log.debug('new virtual storage device created:', msg);
		$scope.refresh();
	}, 'VirtualDisksTab');

	$scope.refresh();
});
