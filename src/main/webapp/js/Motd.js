kerubApp.controller('Motd', function($scope, $http) {
	$http.get('s/r/motd').success(function(motdMarkDown) {
		$scope.motd = markdown.toHTML(motdMarkDown);
	});
});