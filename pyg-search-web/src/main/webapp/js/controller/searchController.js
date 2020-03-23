app.controller('searchController', function ($scope, searchService,$location) {

    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNum': 1,
        'pageSize': 20,
        'sort':'',
        'sortField':''
    };
    $scope.resultMap={};
    //搜索方法
    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                builPageLable();//构建分页栏
                $scope.pageView=$scope.searchMap.pageSize;//每页显示多少个
            }
        )
    };
    //构建分页栏
    builPageLable = function () {
        $scope.pageLabel = [];
        var firstPage = 1; //开始页
        var lastPage = $scope.resultMap.totalPages; //截至页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于5
            if ($scope.searchMap.pageNum <= 3) { //如果当前页码小于3显示前5页
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.searchMap.pageNum >= $scope.resultMap.totalPages - 2) {
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }
        } else {
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };
    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果用户点击的是分类或品牌
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    //解决在选中规格的情况下进行搜索的bug
    $scope.clear=function(){
        $scope.searchMap.category='';
        $scope.searchMap.brand='';
        $scope.searchMap.spec={};
        $scope.searchMap.price='';
        $scope.searchMap.pageSize=20;
        $scope.searchMap.sort='';
        $scope.searchMap.sortField='';

    }
    //撤销搜索项
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果用户点击的是分类或品牌
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //分页搜索
    $scope.queryByPage = function (pageNum) {
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNum = pageNum;
        $scope.search();
    }

    //判断是否是第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNum == 1) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否是最后一页
    $scope.isEndPage = function () {
        if ($scope.resultMap.totalPages == $scope.searchMap.pageNum) {
            return true;
        } else {
            return false;
        }
    }
    //排序查询
    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    }
    //判断关键字是否为品牌（如果含有品牌则隐藏品牌栏）
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//输入的关键字包含品牌
                return true;
            }
        }
        return false;
    }
    //获取路径上的搜索关键词
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['search'];
        console.log($scope.searchMap.keywords);
        if($scope.searchMap.keywords==''||$scope.searchMap.keywords==undefined){
          return;
        }
        $scope.search();
    }
    //跳转到商品详情页
    $scope.getPage = function (goodsId) {
        location.href="http://localhost:8084/#?keywords=" + goodsId;
    }

    //控制一页显示多少个
    $scope.pageView=$scope.searchMap.pageSize;
    //每页显示的数量改变
    $scope.$watch('pageView', function (newValue, oldValue) {
        if($scope.searchMap.keywords==''||$scope.searchMap.keywords==undefined){
            return;
        }
        if(newValue==oldValue){
            return;
        }
        $scope.searchMap.pageSize=newValue*1;
        $scope.searchMap.pageNum=(($scope.searchMap.pageNum-1)*oldValue)/newValue+1;
        $scope.search();
    })
})