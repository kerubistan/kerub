kerubApp.controller('StorageLink', function($scope, $log, appsession, size) {
	$scope.init = function(virtualStorageId) {
		$scope.virtualStorageId = virtualStorageId;
		$scope.loading = true;
		$scope.refresh();
	};

	$scope.refresh = function() {
		appsession.get('s/r/virtual-storage/'+$scope.virtualStorageId).success(function(result) {
			$scope.loading = false;
			$scope.virtualStorage = result;
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
