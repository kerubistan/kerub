var NewVirtualDiskWizard = function($scope, $uibModalInstance, $log, appsession, uuid4, size, expectations, FileUploader) {
	var id = uuid4.generate();

	$scope.uploader = new FileUploader( {
		url : 's/r/virtual-storage/load/' + id
	} );

	$scope.autoname = true;

	$scope.disk = {
		'@type' : 'virtual-storage',
		id : id,
		name : 'disk-'+id,
		size : size.toSize('16 GB'),
		readOnly : false,
		userFriendlySize : function(newSize) {
			if(newSize) {
				$scope.disk.size = size.toSize(newSize);
			}
			return size.humanFriendlySize($scope.disk.size);
		},
		expectations : []
	};

	$scope.checkVDiskName = function() {
		var name = $scope.disk.name;
		if(name.length > 0) {
			$scope.validating = true;
			appsession.get('s/r/virtual-storage/byname/' + name).success(function(results) {
				if(name == $scope.disk.name) {
					$scope.valid = results.length == 0;
					$scope.validating = false;
				}
			});
		} else {
			$scope.valid = false;
			$scope.validating = false;
		}
	};
	$scope.checkVDiskName();

	$scope.isIso = function(item) {
		return item.file.name.toLowerCase().trim().indexOf(".iso") == item.file.name.length - 4;
	};

	$scope.findAutoName = function() {
		appsession.get('s/r/virtual-storage/autoname').then(function(name) {
			if($scope.autoname) {
				$scope.disk.name = name;
			}
		});
	};
	$scope.findAutoName();

	$scope.uploader.onAfterAddingFile = function(item) {
		if($scope.autoname) {
			$scope.disk.name = item.file.name
		}
		if($scope.isIso(item)) {
			$scope.disk.readOnly = true;
		}
		$scope.disk.size = item.file.size;
	};

	$scope.nameChanged = function() {
		$scope.autoname = false;
	};

	/**
	 * gettersetter for disk size
	 */
	$scope.diskSize = function(val) {
		if(val) {
			$log.info(val);
		} else {
		}
	}

	$scope.addStorage = function() {
		appsession.put('s/r/virtual-storage', $scope.disk).success(function(result) {
			if($scope.uploader.queue.length > 0) {
				$scope.uploader.uploadAll();
				$scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {
					$uibModalInstance.dismiss();
				}
			} else {
	        	$uibModalInstance.dismiss();
			}
		});
	};

	$scope.close = function() {
		$uibModalInstance.dismiss();
	};

	$scope.storageExpectationsOpen = false;
	$scope.storageExpectationFormOpen = false;
	$scope.diskExpectations = filterValues(expectations, function(exp) {
		return exp.virtTypes.includes("virtual-storage")
		}
	);
	$scope.openExpectationForm = function(expType) {
		$scope.storageExpectationsOpen = false;
		$scope.storageExpectationFormOpen = true;
		$scope.newExpectation = expType;
	};
	$scope.closeExpectationForm = function() {
		$scope.vmExpectationsOpen = true;
		$scope.vmExpectationFormOpen = false;
		$scope.newExpectation = null;
	};

}
