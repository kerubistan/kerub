kerubApp.factory('size', ['$log', function($log) {
	var mul = 1024;
	var kb = mul;
	var mb = mul * kb;
	var gb = mul * mb;
	var tb = mul * gb;
	var pb = mul * tb;
	var round = function(nr, decimals) {
		return Math.round(nr * Math.pow(10,decimals)) / Math.pow(10,decimals)
	};

	return {

        toSize: function (sizeStr) {
            var clean = sizeStr.toLowerCase().trim();
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
        	if(!size) {
        		return "0 KB";
        	}
        	if(size < kb) {
        		return size + ' B';
        	} else if(size < mb) {
        		return round(size / kb, 1) + ' KB';
        	} else if(size < gb) {
        		return round(size / mb, 1) + ' MB';
        	} else if(size < tb) {
        		return round(size / gb, 1) + ' GB';
        	} else if(size < pb) {
        		return round(size / tb, 1) + ' TB';
        	} else {
        		return round(size / pb, 1) + ' PB';
        	}
        },

        humanFriendlyAccurateSize : function(size) {
        	//TODO
        }

	};
}]);