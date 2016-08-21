
kerubApp.controller('VirtualDisksTab', function($scope, $uibModal, $log, socket, appsession, size) {
	$scope.humanFriendlySize = size.humanFriendlySize;
	$scope.virtualDisks = {};
	$scope.currentPage = 1;
	$scope.itemsPerPage = 10;
	$scope.refresh = function () {
		appsession.get('s/r/virtual-storage?start='
			+ ($scope.itemsPerPage * ($scope.currentPage -1))
			+ '&limit=' + $scope.itemsPerPage
			+ '&sort=name').success(function(result) {
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
