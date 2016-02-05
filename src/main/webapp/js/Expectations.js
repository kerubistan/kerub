kerubApp.factory('expectations', ['$log', '$sce', 'size', function($log, $sce, size) {
	return {
		"cache-size" : {
			"icon" : "fa fa-expand",
			"displayName" : "Cache size",
			"tooltip" : $sce.trustAsHtml("Expects that the VM will run on a host with at least the given amount of cache in the CPU"),
			"shortDescr": function(expectation) {
				return size.humanFriendlySize(expectation.minKbytes * 1024) + '+';
			},
			"virtTypes" : ["vm"]
		},
		"ecc-memory" : {
			"icon" : "fa fa-certificate",
			"displayName" : "ECC memory",
			"tooltip": $sce.trustAsHtml("Expect this VM run on a host that have ECC memory"),
			"shortDescr": function(expectation) {
				return "ECC";
			},
			"virtTypes" : ["vm"]
		},
		"no-migration": {
			"icon" : "glyphicon glyphicon-pushpin",
			"displayName" : "No Migration",
			"tooltip" : $sce.trustAsHtml("Expects that this VM/disk will not be migrated."),
			"shortDescr": function(expectation) {
				return "";
			},
			"virtTypes" : ["vm","vstorage"],
			"template" : "no-migration-edit-template"
		},
		"availability": {
			"icon": 'fa fa-power-off',
			"displayName" : "availability",
			"tooltip" : $sce.trustAsHtml("Expects that this VM will be running/stopped"),
			"shortDescr": function(expectation) {
				return expectation.up ? 'on' : 'off';
			},
			"virtTypes" : ["vm"]
		},
		"storage-redundancy": {
			"icon": 'fa fa-copy',
			"displayName" : "Redundancy",
			"tooltip" : $sce.trustAsHtml("Expects that this virtual storage is located on a <strong>redundant storage</strong> for safety"),
			"shortDescr" : function(expectation) {
				return expectation.nrOfCopies;
			},
			"virtTypes" : ["vstorage"]
		},
		"storage-read-perf" : {
			"icon": 'glyphicon glyphicon-arrow-up',
			"displayName" : "Read performance",
			"tooltip" : $sce.trustAsHtml("Expect that the virtual disk have the given <strong>read performance</strong>"),
			"shortDescr" : function(expectation) {
				return size.humanFriendlySize(expectation.speed.kbPerSec * 1024) + '/s';
			},
			"virtTypes" : ["vstorage"]
		},
		"storage-write-perf": {
			"icon": 'glyphicon glyphicon-arrow-down',
			"displayName" : "Write performance",
			"tooltip" : $sce.trustAsHtml("Expect that the virtual disk have the given <strong>write performance</strong>"),
			"shortDescr" : function(expectation) {
				return 'TODO';
			},
			"virtTypes" : ["vstorage"]
		},
		"cpu-clock-freq": {
			"icon": 'glyphicon glyphicon-flash',
			"displayName" : "CPU clock frequency",
			"tooltip" : $sce.trustAsHtml("Expect the VM to run on a host that has at least the given <strong>CPU clock frequency</strong>"),
			"shortDescr": function(expectation) {
				return expectation.min + ' Mhz +'
			},
			"virtTypes" : ["vm"]
		},
		"ram-clock-freq": {
			"icon": 'glyphicon glyphicon-dashboard',
			"displayName" : "RAM clock frequency",
			"tooltip" : $sce.trustAsHtml("Expect the VM to run on a host that has at least the given <strong>memory clock frequency</strong>"),
			"shortDescr": function(expectation) {
				return expectation.min + ' Mhz +';
			},
			"virtTypes" : ["vm"]
		}
	}
}]);
