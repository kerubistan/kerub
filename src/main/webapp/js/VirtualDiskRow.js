kerubApp.controller('VirtualDiskRow', function($scope, $uibModal, $log, socket, appsession) {
	$scope.init = function(virtDisk) {
		$scope.virtDisk = virtDisk;
	};
	$scope.deleteDisk = function() {
		appsession.delete('s/r/virtual-storage/' + $scope.virtDisk.id);
	};
});