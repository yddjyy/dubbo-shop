var app = angular.module('pyg', []);
app.filter('trustHtml', function ($sce) { //解释html标签
    return function (input) {
        return $sce.trustAsHtml(input);
    }
});