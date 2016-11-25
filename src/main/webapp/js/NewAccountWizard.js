var NewAccountWizard = function($scope, $uibModalInstance, $http, $log, $timeout, appsession, uuid4) {
	$scope.account = {
		'@type' : 'account',
		id : uuid4.generate(),
		name : null,
		requireProjects : false
	};
	$scope.addAccount = function() {
		appsession.put('s/r/accounts',$scope.account).success(
			function() {
				$uibModalInstance.close();
			}
		);
	};
};