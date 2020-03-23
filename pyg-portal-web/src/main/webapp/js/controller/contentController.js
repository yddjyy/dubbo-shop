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
        console.log($scope.category+"搜索"+$scope.keywords);
        location.href="http://localhost:8083/#?search="+$scope.keywords;
    }
})