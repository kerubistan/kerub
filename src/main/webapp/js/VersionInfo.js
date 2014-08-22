kerubApp.controller('VersionInfo', function($scope, $http) {
    $http.get('s/r/meta/version').success(function(versionInfo){
        $scope.versionInfo = versionInfo;
    });
});