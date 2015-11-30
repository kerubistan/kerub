kerubApp.controller('VmRow', function($scope, $log, socket, appsession) {
    $scope.vm = {};
    $scope.vmdyn = {};
    $scope.workingprg = false;
    $scope.startVm = function() {
    	$scope.workingprg = true;
    	appsession.post('s/r/vm/' + $scope.vm.id + '/start').then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.stopVm = function() {
    	$scope.workingprg = true;
    	appsession.post('s/r/vm/' + $scope.vm.id + '/stop').then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.init = function(vm) {
    	$scope.vm = vm;
    	//todo: subscribe events, fill vmdyn
    };
    $scope.destroy = function() {
		//todo: unsubscribe from events
    }
});