kerubApp.controller('BusType', function($scope, $modal, $log) {
	$scope.init = function(link) {
		$log.debug('init', link);
        $scope.link = link;
	};
    $scope.choose = function(value) {
    	$scope.link.bus = value;
    };
    $scope.setBus = function (bus) {
    	$scope.link.bus = bus;
    };
    $scope.isBus = function(bus) {
    	return $scope.link.bus === bus
    }
});