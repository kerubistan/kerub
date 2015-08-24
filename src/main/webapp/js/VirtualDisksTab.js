
kerubApp.controller('VirtualDisksTab', function($scope, $modal, $log, $socket, $appsession) {
	$scope.virtualDisks = {};
	$scope.refresh = function () {
		$appsession.get('s/r/virtual-storage').success(function(result) {
			$scope.virtualDisks = result;
		});
	};

	$scope.humanFriendlySize = function(size) {
		var mul = 1024;
		var kb = mul;
		var mb = mul * kb;
		var gb = mul * mb;
		var tb = mul * gb;
        var pb = mul * tb;
		if(size < kb) {
			return size + ' B';
		} else if(size < mb) {
			return Math.round(size / kb) + ' KB';
		} else if(size < gb) {
			return Math.round(size / mb) + ' MB';
		} else if(size < tb) {
			return Math.round(size / gb) + ' GB';
		} else if(size < pb) {
			return Math.round(size / tb) + ' TB';
		} else {
			return Math.round(size / pb) + ' PB';
		}
	};

	$scope.expectationLevel = function(expectationLevel) {
		switch(expectationLevel) {
			case 'Hint':
				return 'success';
			case 'Want':
				return 'info';
			case 'DealBreaker':
				return 'warning';
			default:
				$log.error('Unknown expectation level', expectationLevel);
				return 'error';
		}
	}

	$scope.shortExpectation = function(expectation) {
		switch(expectation['@type']) {
			case 'storage-redundancy':
				return expectation.nrOfCopies;
			case 'storage-read-perf':
				return $scope.humanFriendlySize(expectation.speed.kbPerSec * 1024);
		}
	};

	$scope.expectationIcon = function(expectation) {
		switch(expectation['@type']) {
			case 'storage-redundancy':
				return 'hdd';
			case 'storage-read-perf':
				return 'arrow-up';
			case 'storage-write-perf':
				return 'arrow-down'
			default:
				return 'cog';
		}
	};

	$scope.refresh();
});
