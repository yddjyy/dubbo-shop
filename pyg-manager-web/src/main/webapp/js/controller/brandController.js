app.controller('brandController', function ($scope, $http, $controller, brandService) {
    $controller('baseController', {$scope: $scope});
    $scope.findAll = function () {
        brandService.findAll().success(
            function (data) {
                $scope.list = data;
            }
        )
    };


    //分页
    // $scope.findPage = function (page, rows) {
    //     $http.get('../brand/findPage?page=' + page + '&rows=' + rows).success(function (data) {
    //         $scope.list = data.rows;
    //         $scope.paginationConf.totalItems = data.total;//更新总记录数
    //     });
    // };

    //更新 添加
    $scope.save = function () {
        var obj = null;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(
            function (data) {
                if (data.success) {
                    $scope.reloadList();
                } else {
                    alert(data.message);
                }
                $scope.entity = null;
            }
        )
    };
    //按ID查找
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (data) {
                $scope.entity = data;
            }
        )
    };
    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(
            function (data) {
                if (data.success) {
                    $scope.reloadList();
                } else {
                    alert(data.message);
                }
            }
        )
    };
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search($scope.searchEntity, page, rows).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;//更新总记录数
                $scope.pageChange();
            }
        )
    };
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
})