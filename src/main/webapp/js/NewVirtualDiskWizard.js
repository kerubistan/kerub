var NewVirtualDiskWizard = function($scope, $uibModalInstance, $log, appsession, uuid4, size, expectations, FileUploader) {
	$scope.id = uuid4.generate();

	$scope.uploader = new FileUploader( {
	} );

	$scope.autoname = true;

	$scope.disk = {
		'@type' : 'virtual-storage',
		id : $scope.id,
		name : 'disk-'+$scope.id,
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

	$scope.fileFormat = 'raw';

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

	$scope.fileExtension = function(item) {
		var name = item.file.name.toLowerCase().trim();
		var lastDot = name.lastIndexOf('.');
		if(lastDot < 0) {
			return null;
		} else {
			return name.substring(lastDot + 1);
		}
	};

	$scope.isIso = function(item) {
		return $scope.fileExtension(item) == "iso"
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
		$scope.disk.size = item.file.size;
		var fileExtension = $scope.fileExtension(item);
		$scope.disk.readOnly = false;
		switch(fileExtension) {
			case 'qcow2':
				$scope.fileFormat = 'qcow2';
				break;
			case 'vmdk':
				$scope.fileFormat = 'vmdk';
				break;
			case 'vdi':
				$scope.fileFormat = 'vdi';
				break;
			case 'iso':
				$scope.fileFormat = 'raw';
				$scope.disk.readOnly = true;
				break;
			default:
				$scope.fileFormat = 'raw';
				break;
		}
		item.url = 's/r/virtual-storage/load/' + $scope.fileFormat + '/' + $scope.id;
	};

	$scope.nameChanged = function() {
		$scope.autoname = false;
	};

	$scope.addStorage = function() {
		appsession.put('s/r/virtual-storage', $scope.disk).success(function(result) {
			if($scope.uploader.queue.length > 0) {
				$scope.uploader.uploadAll($scope.uploader.url);
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
