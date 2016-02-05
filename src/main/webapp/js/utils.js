
/**
 *
 */
function getStart(pagenum, itemsPerPage) {
    return (pagenum - 1) * itemsPerPage;
}

/**
 * Returns a list of pages based on the result page.
 */
function pages(resultPage, itemsPerPage) {
    var pages = [];
    for(var i = 0; resultPage.total > itemsPerPage * i ; i++) {
        pages.push(i + 1);
    }
    return pages;
}

/**
 * Return the current page in the
 */
function currentPage(resultPage, itemsPerPage) {
    return (resultPage.start / itemsPerPage) + 1;
}

function filterValues(obj, valueFilter) {
	var results = [];
	for(var prop in obj) {
		if(obj.hasOwnProperty(prop) && valueFilter(obj[prop])) {
			results.push(obj[prop]);
		}
	}
	return results;
}