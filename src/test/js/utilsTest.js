describe("pages function", function() {
    it("should return list of pages", function() {
        var p = pages({start : 0, count : 2, total : 10, sort : 'id', result : [{},{}]}, 2);
        expect(p.length).toBe(5);
    });
    it("should return list of pages", function() {
        var p = pages({start : 0, count : 2, total : 11, sort : 'id', result : [{},{}]}, 2);
        expect(p.length).toBe(6);
    });
    it("return function should be from one to N", function() {
        var p = pages({start : 0, count : 2, total : 10, sort : 'id', result : [{},{}]}, 2);
        for(var i = 0; i < p.length; i++) {
            expect(p[i]).toBe(i+1);
        }
    });
});

describe("currentPage function", function() {
    it("should return the current page", function() {
        expect(currentPage({start : 0, count : 2, total : 10, sort : 'id', result : [{},{}]}, 2)).toBe(1);
        expect(currentPage({start : 2, count : 2, total : 10, sort : 'id', result : [{},{}]}, 2)).toBe(2);
        expect(currentPage({start : 4, count : 2, total : 10, sort : 'id', result : [{},{}]}, 2)).toBe(3);
    });
});

describe("getStart function", function() {
    it("should return correct number of start based on page and items/page", function() {
        expect(getStart(1,10)).toBe(0);
    });
});