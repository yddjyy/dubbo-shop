//控制层
app.controller('goodsController', function ($scope, $controller, itemCatService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
                $scope.pageChange();
            }
        );
    }
    $scope.pageChange=function(){
        //翻页
        $(".zxf_pagediv").createPage({
            pageNum: Math.ceil($scope.paginationConf.totalItems/$scope.paginationConf.itemsPerPage),
            current: $scope.paginationConf.currentPage,
            backfun: function(e) {
                $scope.paginationConf.currentPage=e.current;
                $scope.reloadList();
            }
        });
    }
    //审核状态
    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];

    $scope.itemCatList = [];
    //查询商品分类
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (data) {
                for (var i = 0; i < data.length; i++) {
                    $scope.itemCatList[data[i].id] = data[i].name;
                }
            }
        )
    }
    //更新状态
    $scope.updateStatus = function (status) {
        if($scope.selectIds.length==0)
        {
            alert("请先选择商品！");
            return;
        }
        goodsService.updateStatus($scope.selectIds, status).success(
            function (data) {
                if (data.success) {
                    $scope.reloadList();
                    $scope.selectIds = [];
                }else{
                    alert(data.message);
                }
            }
        )
    }
});	
