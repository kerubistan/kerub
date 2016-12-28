kerubApp.controller('AttachedVms', function($scope, $log, appsession, size) {
	$scope.init = function(storageId) {
		$scope.storageId = storageId;
		$scope.loading = true;
		$scope.refresh();
	}
	$scope.refresh = function() {
		appsession.get('s/r/vm/connected-to/'+$scope.storageId).success(function(result) {
			$scope.vms = result;
			$scope.loading = false;
		});
	}
	$scope.shortName = function(name, size) {
		if(name.length > size) {
			return name.substr(0,size - 3) + '...';
		} else {
			return name;
		}
	};


});
