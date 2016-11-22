kerubApp.controller('MainTabs', function($scope, appsession, $log) {
	$scope.user = {
		name : null,
		roles : []
	};
	$scope.refresh = function() {
		appsession.get('s/r/auth/user').success(function(result) {
			$scope.user = result;
		});
	};
	$scope.isAdmin = function() {
		return $scope.user.roles.indexOf('Admin') >= 0;
	}
	$scope.refresh();
});

