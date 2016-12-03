var NewProjectWizard = function($scope, $uibModalInstance, $http, $log, $timeout, appsession, uuid4) {
	$scope.project = {
		'@type' : 'project',
		id : uuid4.generate(),
		name : null,
		description : null
	};

	$scope.listAccounts = function() {

	};
};
