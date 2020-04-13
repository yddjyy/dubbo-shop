//控制层
app.controller('orderController', function ($scope,orderService) {

    $scope.page={
        currentPage:1,
        pageSize:15,
        total:0
    }
    $scope.init=function () {
        $scope.findAllByPage($scope.page.currentPage,$scope.page.pageSize);
    }

    $scope.findAllByPage=function (currentPage,pageSize) {
        console.log(currentPage);
        orderService.findAllByPage(currentPage,pageSize).success(
            function (response) {
                console.log(response);
                $scope.orderList=response.rows;
                $scope.page.total=Math.ceil(response.total/$scope.page.pageSize);
                builPageLable();
            }
        )
    }
    //分页搜索
    $scope.queryByPage = function (pageNum) {
        if (pageNum < 1 || pageNum > $scope.page.total) {
            return;
        }
        $scope.page.currentPage = pageNum;
        $scope.findAllByPage($scope.page.currentPage,$scope.page.pageSize);
    }
    //判断是否是第一页
    $scope.isTopPage = function () {
        if ($scope.page.currentPage == 1) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否是最后一页
    $scope.isEndPage = function () {
        if ($scope.page.total == $scope.page.currentPage) {
            return true;
        } else {
            return false;
        }
    }
    $scope.PresendGoods=function(orderId){
        $scope.orderId=orderId;
    }
    //点击发货
    $scope.sendGoods=function () {
        orderService.sendGoods($scope.orderId,'12345678911').success(
            function (response) {
                console.log(response);
            }
        )
    }
    //查看商品信息
    $scope.viewGoods=function (orderId) {
        console.log(orderId);
       orderService.viewGoods(orderId).success(
           function (response) {
                console.log(response);
           }
       )
    }

    //构建分页栏
    builPageLable = function () {
        $scope.pageLabel = [];
        var firstPage = 1; //开始页
        var lastPage = $scope.page.total; //截至页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.page.total > 5) { //如果总页数大于5
            if ($scope.page.currentPage <= 3) { //如果当前页码小于3显示前5页
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.page.pageNum >= $scope.page.total - 2) {
                firstPage = $scope.page.total - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.page.currentPage - 2;
                lastPage = $scope.page.currentPage + 2;
            }
        } else {
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };



    //退款订单
    $scope.refundOrder=function () {
        console.log("初始化");
        $scope.findRefundOrderBySellerId();
    }

    $scope.findRefundOrderBySellerId=function () {
        orderService.findRefundOrderBySellerId().success(
            function (response) {
                $scope.refundList=response;
                console.log(response);
            }
        )

    }
    //查看退款描述
    $scope.viewRefundOrder=function (refundId) {
        orderService.viewRefundOrder(refundId).success(
            function (resposne) {
                $scope.refundItem=resposne;
                console.log($scope.refundItem);
                $scope.images=$scope.refundItem.images.split(",");
            }
        )
    }
    $scope.getId=function(refundId){
        $scope.refundId=refundId;
    }
    //退款原因
    $scope.reason=['','','不想要了','买错了','没收到货','与说明不符'];
    //不同意退款
    $scope.returnRefund=function () {
        orderService.returnRefund($scope.refundId,$scope.reply).success(
            function (response) {
                if(response.success){
                    $scope.findRefundOrderBySellerId();
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //同意退款
    $scope.refundOk=function (refundId,refund_amount,outTradeNo) {
        orderService.returnOk(refundId,refund_amount,outTradeNo).success(
            function (response) {
                console.log(response);
                if(response.success){
                    $scope.findRefundOrderBySellerId();
                }else{
                    alert(response.message);
                }
            }
        )
        console.log(refundId+"同意");
    }


    //查看历史记录
    $scope.historyOrder=function () {
        orderService.findHistoryByPage($scope.page.currentPage,2).success(
            function (response) {
                console.log(response);
            }
        )
    }
});
