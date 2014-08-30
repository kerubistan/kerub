describe("Version Info Module", function() {

    var scope, ctrl, $httpBackend;

    beforeEach(module('kerubApp'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('s/r/meta/version')
            .respond({version : 'TEST-VERSION', vendor : 'TEST-VENDOR', title : 'TEST-TITLE'});
        scope = $rootScope.$new();
        ctrl = $controller('VersionInfo', {$scope : scope});
    }));

    it("should get version info from server with rest api call", function() {
        expect(scope.versionInfo).toBeUndefined();
        $httpBackend.flush();
        expect(scope.versionInfo.version).toBe('TEST-VERSION');
        expect(scope.versionInfo.vendor).toBe('TEST-VENDOR');
        expect(scope.versionInfo.title).toBe('TEST-TITLE');
    });
});