app.controller('contentController', function ($scope, contentService) {
    $scope.contentList = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (data) {
                $scope.contentList[categoryId] = data;
            }
        )
    }
    $scope.search = function () {
        location.href="http://localhost:8083/#?keywords="+$scope.keywords;
    }
})