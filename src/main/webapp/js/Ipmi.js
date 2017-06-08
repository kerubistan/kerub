kerubApp.controller('Ipmi', function($scope, appsession) {

	$scope.config = {
		"@type" : "ipmi",
		"address" : null,
		"username" : null,
		"password" : null
	}

	$scope.validationError = null;

	$scope.addressValidationRunning = false;

	$scope.init = function(config) {
		console.log("init!", config);
		$scope.config = config;
	};

	$scope.validate = function() {
		$scope.validationError = null;
		$scope.addressValidationRunning = true;
		appsession.get('s/r/helpers/power/ipmi/ping?address='+$scope.config.address)
			.success(function(data) {
				$scope.validationError = null;
				$scope.addressValidationRunning = false;
			})
			.error(function(error) {
				$scope.validationError = error;
				$scope.addressValidationRunning = false;
			})
	};

});