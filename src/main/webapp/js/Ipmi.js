kerubApp.controller('Ipmi', function($scope, appsession) {

	$scope.config = {
		"@type" : "ipmi",
		"address" : null,
		"username" : null,
		"password" : null
	}

	$scope.container = [];

	$scope.validationError = null;

	$scope.init = function(config, container) {
		$scope.config = config;
		$scope.container = container;
	};

	$scope.remove = function() {
		$scope.container
	}

	$scope.validate = function() {
		appsession.get('s/r/helpers/power/ipmi/ping', {address : $scope.config.address})
			.success(function(data) {
				$scope.validationError = null;

			})
	};
});