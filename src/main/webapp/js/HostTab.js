kerubApp.controller('HostTab', function($scope, $http) {
    $scope.newHostForm = function() {

    };
    $http.get('s/r/host.json').success(function(hosts) {
        $scope.hosts = hosts
    });
});