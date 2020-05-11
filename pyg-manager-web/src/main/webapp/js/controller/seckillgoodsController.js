//控制层
app.controller('seckillgoodsController', function ($scope, $controller, itemCatService,GoodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        GoodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        GoodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        GoodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = GoodsService.update($scope.entity); //修改
        } else {
            serviceObject = GoodsService.add($scope.entity);//增加
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
        GoodsService.dele($scope.selectIds).success(
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
        GoodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                console.log($scope.list);
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

    //更新状态
    $scope.updateStatus = function (status) {
        if($scope.selectIds.length==0)
        {
            alert("请先选择商品！");
            return;
        }
        GoodsService.updateStatus($scope.selectIds, status).success(
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
