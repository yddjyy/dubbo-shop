app.controller('orderController',function($scope,loginService,orderService,payService,$location){
    $scope.init=function(status){
        $scope.showName();
        $scope.flag=0;//是否支付成功，用来判断是否显示支付成功时的图片
        $scope.waitPayment_len=0;//是否有待付款订单，用来判断当前页是否显示商品列表标题头
    }
    //查询当前用户名
    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
                $scope.showAllOrder();//显示所有历史订单
            }
        );
    }

    //点击所有订单
    $scope.showAllOrder=function () {
        $scope.recoveryStatu();//恢复初始状态
        $scope.pageConf.status=8;//订单状态
        $scope.findPage($scope.pageConf.currentPage,$scope.pageConf.perPage,$scope.pageConf.status);
    }

    //json转换
    $scope.praseJson=function(jsonStr){
        return  eval('(' + jsonStr + ')');
    }

    //取消订单
    $scope.cancelOrder=function(outTradeNo){
        console.log("取消："+outTradeNo);
        orderService.cancelOrder(outTradeNo).success(
            function (response) {
                if(response.success){
                    //操作成功
                    $scope.waitPayment();//刷新待付款页面数据
                }else{
                    alert(response.message);
                }
            }
        )
    }

    //点击待付款
    $scope.waitPayment=function(){
       orderService.waitPayment().success(
            function (response) {
                 console.log(response);
                 $scope.waitPaymentOrder=response;
                 $scope.waitPayment_len= $scope.waitPaymentOrder.length;
            }
        )
    }
    //一键支付
    $scope.payMent=function(outTradeNo,totalFee){
        console.log("支付："+outTradeNo+","+totalFee);
        $scope.createNative(outTradeNo,totalFee);
    }
    //创建二维码
    $scope.createNative=function(outTradeNo,totalFee){
        payService.createNative(outTradeNo,totalFee).success(
            function(response){
                //显示订单号和金额
                $scope.money= response.total_fee;
                $scope.out_trade_no=response.out_trade_no;

                //生成二维码
                var qr=new QRious({
                    element:document.getElementById('qrious'),
                    size:300,
                    value:response.code_url,
                    level:'H'
                });

                queryPayStatus();//调用查询

            }
        );
    }

    //调用查询
    queryPayStatus=function(){
        payService.queryPayStatus($scope.out_trade_no).success(
            function(response){
                console.log(response.message);
                if(response.success){
                    $scope.flag=1;
                    console.log("支付成功");
                    $scope.refresh();//刷新待支付的数据
                }else{
                    if(response.message=='二维码超时'){
                        $scope.createNative();//重新生成二维码
                    }else{
                        alert("支付失败，稍后重试");
                    }
                }
            }
        );
    }
    //分页控件配置
    $scope.pageConf = {
        currentPage: 1,//当前页码
        clickTotal: 1,//点击更多的次数
        perPage: 2,//每页显示多少
        status:2,//查询的订单状态
        totalSize:2//当前页面总大小
    };
    //点击每个选项卡时将当前页标识恢复到初始状态1,清空orderlist容器
    $scope.recoveryStatu=function(){
        $scope.pageConf.currentPage==1;
        $scope.orderList="";
        $scope.pageConf.clickTotal=1;
        $scope.pageConf.totalSize=2;
    }

    //获取更多
    $scope.getMoreInfo=function(){
        $scope.pageConf.clickTotal+=1;//点击的次数加1
        $scope.pageConf.totalSize = $scope.pageConf.perPage*$scope.pageConf.clickTotal
        $scope.findPage($scope.pageConf.currentPage,$scope.pageConf.totalSize,$scope.pageConf.status);
    }
    //是否为最后一页
    $scope.isEndPage=function(){
        if($scope.total>$scope.pageConf.currentPage*$scope.pageConf.totalSize)
            return false;
        else return true;
    }
    //刷新
    $scope.refresh=function(){
        $scope.waitPayment();
    }

    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }


    //点击待发货
    $scope.waitSendGoods=function () {
        $scope.recoveryStatu();//恢复初始状态
        $scope.pageConf.status=2;//订单状态
        $scope.findPage($scope.pageConf.currentPage,$scope.pageConf.perPage,$scope.pageConf.status);
    }

    //点击待收货
    $scope.waitReceiveGoods=function () {
        $scope.recoveryStatu();//恢复初始状态
        $scope.pageConf.status=4;//订单状态
        $scope.findPage($scope.pageConf.currentPage,$scope.pageConf.perPage,$scope.pageConf.status);
    }

    //点击待评价
    $scope.waitComments=function () {
        $scope.recoveryStatu();//恢复初始状态
        $scope.pageConf.status=7;//订单状态
        $scope.findPage($scope.pageConf.currentPage,$scope.pageConf.perPage,$scope.pageConf.status);
    }

    //根据状态分页查找
    $scope.findPage = function (pageNum,pageSize,status) {
        orderService.findOrderByStatusAndPage($scope.loginName,pageNum,pageSize,status).success(
            function (response) {
                $scope.orderList=response.rows;
                console.log($scope.orderList);
                $scope.total=response.total;
            }
        );
    }

    //删除订单
    $scope.delOrderByOrderId=function (orderId) {
        console.log(orderId);
        orderService.delOrderByOrderId(orderId).success(
            function (response) {
                if(response.success){
                    //如果成功刷新数据
                    $scope.showAllOrder();
                }else{
                    alert(response.message);
                }
            }
        )
    }




    //订单详情页
    $scope.showOrderDetailInfo=function () {
        $scope.showName();
    }

    //获取订单id和订单状态
    $scope.getOrderIdAndStatus=function () {
       $scope.orderId= $location.search()['orderId'];
       $scope.status= $location.search()['status'];
        //获取订单id后才能进行查询，防止报错；
        $scope.findAddressByOrderId();
        $scope.findOrderDetailInfoByOrderId();
    }

    //查看订单地址
    $scope.findAddressByOrderId=function () {
        orderService.findAddressByOrderId($scope.orderId).success(
            function (response) {
                $scope.address=response
            }
        )
    }

    //获取订单详细信息
    $scope.findOrderDetailInfoByOrderId=function () {
        orderService.findOrderDetailInfoByOrderId($scope.orderId).success(
            function (response) {
                $scope.orderDetailList=response;
            }
        )
    }

});