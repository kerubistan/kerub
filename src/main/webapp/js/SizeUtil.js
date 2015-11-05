kerubApp.factory('size', ['$log', function($log) {
	var mul = 1024;
	var kb = mul;
	var mb = mul * kb;
	var gb = mul * mb;
	var tb = mul * gb;
	var pb = mul * tb;
	return {

        toSize: function (sizeStr) {
            clean = sizeStr.toLowerCase().trim();
            var amount = parseFloat(clean);
            if(clean.endsWith('kb')) {
				return amount * kb;
            } else if(clean.endsWith('mb')) {
                return amount * mb;
            } else if(clean.endsWith('gb')) {
                return amount * gb;
            } else if(clean.endsWith('tb')) {
                return amount * tb;
            } else if(clean.endsWith('pb')) {
                return amount * pb;
            } else {
            	return amount;
            }
        },

        humanFriendlySize : function(size) {
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