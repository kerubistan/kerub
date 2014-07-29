kerubApp.controller('HostTab', function($scope, $http, $modal) {
    $scope.newHostForm = function() {
        $modal.open({
            templateUrl : 'NewHostWizard'
        });
    };
    $http.get('s/r/host.json').success(function(hosts) {
        $scope.hosts = hosts
    });
});