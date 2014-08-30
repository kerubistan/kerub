describe('HostTab', function() {
    var scope, ctrl, $httpBackend;

    beforeEach(module('kerubApp'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('s/r/host')
            .respond([]);
        scope = $rootScope.$new();
        ctrl = $controller('HostTab', {$scope : scope});
    }));

    it('should load the list of hosts', function() {

    });
});