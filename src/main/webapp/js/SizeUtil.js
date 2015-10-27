kerubApp.factory('size', ['$log', function($log) {
	return {
        humanFriendlySize : function(size) {
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
        }
	};
}]);