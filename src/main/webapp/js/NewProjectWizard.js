var NewProjectWizard = function($scope, $uibModalInstance, $http, $log, $timeout, appsession, uuid4) {
	$scope.project = {
		'@type' : 'project',
		id : uuid4.generate(),
		name : '',
		description : '',
		expectations : [],
		owner : null
	};

	$scope.account = null;

	$scope.listAccounts = function(name) {
		return $http.get('s/r/accounts/search?field=name&value='+name).then(function(result){
			return result.data.result;
		});
	};

	$scope.selectAccount = function() {
		$scope.project.owner = {
			ownerId : $scope.account.id,
			ownerType : 'account'
		};
	};

	$scope.addProject = function() {
		appsession.put('s/r/projects', $scope.project).then(function() {
			$uibModalInstance.close();
		});
	};

	$scope.close = function() {
		$uibModalInstance.dismiss('cancel');
	};

};
