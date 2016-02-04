kerubApp.factory('expectations', ['$log', 'size', function($log, size) {
	return {
		"cache-size" : {
			"icon" : "fa fa-expand",
			"shortDescr": function(expectation) {
				return size.humanFriendlySize(expectation.minKbytes * 1024) + '+';
			}
		},
		"ecc-memory" : {
			"icon" : "fa fa-certificate",
			"shortDescr": function(expectation) {
				return "ECC";
			}
		},
		"no-migration": {
			"icon" : "glyphicon glyphicon-pushpin",
			"shortDescr": function(expectation) {
				return "";
			}
		},
		"availability": {
			"icon": 'fa fa-power-off',
			"shortDescr": function(expectation) {
				return expectation.up ? 'on' : 'off';
			}
		},
		"storage-redundancy": {
			"icon": 'glyphicon glyphicon-duplicate',
			"shortDescr" : function(expectation) {
				return expectation.nrOfCopies;
			}
		},
		"storage-read-perf" : {
			"icon": 'glyphicon glyphicon-arrow-up',
			"shortDescr" : function(expectation) {
				return size.humanFriendlySize(expectation.speed.kbPerSec * 1024) + '/s';
			}
		},
		"storage-write-perf": {
			"icon": 'glyphicon glyphicon-arrow-down',
			"shortDescr" : function(expectation) {
				return 'TODO';
			}
		},
		"cpu-clock-freq": {
			"icon": 'glyphicon glyphicon-flash',
			"shortDescr": function(expectation) {
				return expectation.min + ' Mhz +'
			}
		},
		"ram-clock-freq": {
			"icon": 'glyphicon glyphicon-dashboard',
			"shortDescr": function(expectation) {
				return expectation.min + ' Mhz +';
			}
		},
		"cache-size": {
			"icon": 'glyphicon glyphicon-plus-sign',
			"shortDescr": function(expectation) {
				return size.humanFriendlySize(expectation.minKbytes * 1024) + '+';
			}
		}
	}
}]);
