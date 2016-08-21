
kerubApp.controller('HostTab', function($scope, $uibModal, $log, socket, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;

	$scope.hosts = {};
    $scope.itemsPerPage = 10;
    $scope.currentPage = 1;

    $scope.newHostForm = function() {
        var modalInstance = $uibModal.open({
            templateUrl : 'NewHostWizard.html',
            controller : NewHostWizard
        });
        $log.debug(modalInstance);
    };

	$scope.refresh = function() {
		appsession.get('s/r/host?start='
			+ ($scope.itemsPerPage * ($scope.currentPage -1))
			+ '&limit=' + $scope.itemsPerPage
			+ '&sort=address').success(function(hostsResult) {
			$scope.hosts = hostsResult;
		});
	}
	$scope.refresh();
    socket.subscribe('/host', function(msg) {
        $scope.refresh();
    }, 'HostTab');


    $scope.showHostDetails = function(hostId) {
        var modalInstance = $uibModal.open({
            templateUrl : 'HostDetails.html',
            controller : HostDetails,
            resolve : {
                hostId : function() {
                    return hostId;
                }
            }
            });
    }


});
