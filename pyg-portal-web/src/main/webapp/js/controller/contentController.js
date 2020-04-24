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
    //加载打分最多的商品
    $scope.loadRate=function () {
        console.log("评分最多");
       contentService.loadRate(20,"").success(
           function (response) {
                console.log(response);
                $scope.rate=response.products;
           }
       )
    }
    //获取热门推荐
    $scope.loadHot=function () {
        console.log("热门推荐");
        contentService.loadHot(20,"").success(
            function (response) {
                console.log(response);
                $scope.hot=response.products;
            }
        )
    }
    //加载离线推荐
    $scope.loadOffline=function () {
        console.log("离线推荐");
        contentService.loadOffline("abc",20,"").success(
            function (response) {
                console.log(response);
                $scope.offline=response.products;
            }
        )
    }
    $scope.flag=0;
    //实时推荐
    $scope.loadStream=function () {
        console.log("实时推荐");
        contentService.loadStream("abc",20,"").success(
            function (response) {
                console.log(response);
                $scope.stream=response.products;
                $scope.flag=1;
                $scope.test=[1,3,4,5,6,7];
            }
        )
    }
})