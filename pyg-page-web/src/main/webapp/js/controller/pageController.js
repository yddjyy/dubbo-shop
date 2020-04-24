app.controller('pageController', function ($scope, pageService,$location,$sce) {

    $scope.getPage = function (goodsId) {
        pageService.getPage(goodsId).success(
            function (data) {
                $scope.goods = data;
                $scope.goods.goodsDesc.itemImages = JSON.parse($scope.goods.goodsDesc.itemImages);
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse($scope.goods.goodsDesc.customAttributeItems);
                $scope.goods.goodsDesc.specificationItems = JSON.parse($scope.goods.goodsDesc.specificationItems);
                $scope.skuList = data.itemList;
                $scope.sku = $scope.skuList[0];
                $scope.selectItems = JSON.parse(JSON.stringify(JSON.parse($scope.skuList[0].spec)));
                console.log( $scope.skuList)
            }
        )
    };
    //匹配两个对象
    matchObject=function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    };
    //查询SKU
    searchSku=function(){
        for(var i=0;i<$scope.skuList.length;i++ ){
            if( matchObject(JSON.parse($scope.skuList[i].spec) ,$scope.selectItems ) ){
                $scope.sku=$scope.skuList[i];
                return ;
            }
        }
        $scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
    };
    //添加商品到购物车
    $scope.addToCart=function(){
        pageService.addToCart($scope.sku.id,$scope.num);
    }


    $scope.loadPage = function () {
        $scope.spuid= $location.search()['keywords'];
        $scope.getPage( $scope.spuid);
        $scope.loadContentBase($scope.spuid);
        $scope.loadItemCf($scope.spuid);
    };

    $scope.selectItems = {};//存储用户选择的内容
    $scope.addSelectItems = function (x,y) {
        $scope.selectItems[x] = y;
        searchSku();//读取sku
    };
    //判断某规格是否选中
    $scope.isSelect = function(x,y){
        if ($scope.selectItems[x] == y){
            return true;
        }else {
            return false;
        }
    };

    $scope.num = 1;
    //数量加减
    $scope.addNum = function (x) {
        $scope.num += x;
        if($scope.num <= 1){
            $scope.num = 1;
        }
        if($scope.num>$scope.sku.num){
            $scope.num=$scope.sku.num;
        }
    }

    //获取评论信息
    $scope.getCommentsInfo=function () {
        pageService.getCommentsInfo($scope.spuid,1,15).success(
            function (response) {
                $scope.commentsItem=response.rows;
            }
        )
    }

    //时间格式转换 时间戳-> 日期
    $scope.reverse=function (value) {
        var date = new Date(value*1000);
        Y = date.getFullYear() + '-';
        M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        D = date.getDate() + ' ';
        return Y+M+D;
    }
    $scope.contentflag=0;
    //加载基于内容的推荐模块
    $scope.loadContentBase=function (goodsId) {
        console.log("加载基于内容的推荐模块："+goodsId);
        pageService.loadContentBase(109236,"").success(
            function (response) {
                console.log(response);
                $scope.contentbase=response.products;
                $scope.contentflag=1;
            }
        )
    }
    $scope.itemcfflag=0;
    //加载基于物品的推荐模块
    $scope.loadItemCf=function (goodsId) {
        console.log("加载基于内容的推荐模块："+goodsId);
        pageService.loadItemCf(109236,"").success(
            function (response) {
                console.log(response);
                $scope.itemcf=response.products;
                $scope.itemcfflag=1;
            }
        )
    }
});
app.filter('trustHtml', function ($sce) {
    return function (input) {
        return $sce.trustAsHtml(input);
    }
});