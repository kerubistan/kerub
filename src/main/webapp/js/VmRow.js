kerubApp.controller('VmRow', function($scope, $log, $socket, $appsession) {
    $scope.vm = {};
    $scope.vmdyn = {};
    $scope.init = function(vm) {
    	$scope.vm = vm;
    	//todo: subscribe events, fill vmdyn
    };
    $scope.destroy = function() {
		//todo: unsubscribe from events
    }
});