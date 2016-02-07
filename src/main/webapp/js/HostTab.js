
kerubApp.controller('HostTab', function($scope, $uibModal, $log, socket, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;

    $log.info('initializing host tab');

    socket.subscribe('/host', function(msg) {
        $log.info("hey, a message for HostTab!",msg);
    }, 'HostTab');

    $scope.itemsPerPage = 10;
    $scope.currentPage = 1;
    $scope.pages = [];

    $scope.newHostForm = function() {
        $log.info('opening new host wizard');
        var modalInstance = $uibModal.open({
            templateUrl : 'NewHostWizard.html',
            controller : NewHostWizard
        });
        modalInstance.result.then(function() {
            $log.info('kakukk');
        });
        $log.debug(modalInstance);
        $log.info('opened new host wizard');
    };

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

    appsession.get('s/r/host').success(function(hostsResult) {
        $log.info("hosts", hostsResult);
        $scope.hosts = hostsResult.result;
        $scope.currentPage = currentPage(hostsResult, $scope.itemsPerPage);
        $log.debug($scope.currentPage);
        $scope.pages = pages(hostsResult, $scope.itemsPerPage);
    });
});
