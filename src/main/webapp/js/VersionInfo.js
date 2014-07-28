kerubApp.controller('VersionInfo', function($scope, $http) {
    $http.get('s/r/meta/version.json').success(function(versionInfo){
        $scope.versionInfo = versionInfo;
    });
});