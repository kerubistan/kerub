kerubApp.controller('AccountsAdmin', function($scope, appsession, $uibModal) {
	$scope.accounts = {};

	$scope.openNewAccountDialog = function() {
		$uibModal.open({
			templateUrl : 'CreateAccount.html',
			controller : NewAccountWizard
		});
	};

	$scope.openNewProjectDialog = function() {
		$uibModal.open({
			templateUrl : 'CreateProject.html',
			controller : NewProjectWizard
		});
	};

	$scope.refresh = function() {
		appsession.get('s/r/accounts').success(function(result) {
			$scope.accounts = result;
		});
	};
	$scope.refresh();
});