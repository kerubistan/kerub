describe('HostTab', function() {
    var scope, ctrl, $httpBackend;

    beforeEach(module('kerubApp'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('s/r/host')
            .respond({start : 0, count : 1, total : 5 ,result:[{address: '127.0.0.1'}]});
        scope = $rootScope.$new();
        ctrl = $controller('HostTab', {$scope : scope});
    }));

    it('should load the list of hosts', function() {
        expect(scope.hosts).toBeUndefined();
        $httpBackend.flush();
        expect(scope.hosts.length).toBe(1);
        expect(scope.hosts[0].address).toBe('127.0.0.1');
    });
});