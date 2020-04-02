app.controller('refundController',function($scope,loginService,refundService,orderService,$location,uploadService){

    $scope.init=function () {
        $scope.showName();
       $scope.getOrderIdAndOutTradeNo();//获取地址栏信息
        $scope.entity='';
    }

    //json转换
    $scope.praseJson=function(jsonStr){
        return  eval('(' + jsonStr + ')');
    }

    //查询当前用户名
    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

    //获取订单id和订单状态
    $scope.getOrderIdAndOutTradeNo=function () {
        $scope.outTradeNo= $location.search()['outTradeNo'];
        $scope.orderItemId= $location.search()['orderItemId'];
        $scope.sellerId= $location.search()['sellerId'];
        //获取订单id后才能进行查询，防止报错；
        $scope.findOrderDetailInfoByOrderItemId();
        $scope.findRefundOrderByOrderItemId();
    }

    //获取订单详细信息
    $scope.findOrderDetailInfoByOrderItemId=function () {
        refundService.findOrderDetailInfoByOrderItemId($scope.orderItemId).success(
            function (response) {
                $scope.orderDetailList=response;
                $scope.price=$scope.orderDetailList.orderItems[0].price*$scope.orderDetailList.orderItems[0].num;
            }
        )
    }
    //从退款列表中查看是否有当前订单的退款信息
    $scope.findRefundOrderByOrderItemId=function(){
        refundService.findRefundOrderByOrderItemId($scope.orderItemId).success(
            function (response) {
                $scope.tbRefund=response;
                console.log(response);
                if(response==''){
                   $scope.flag=0;//还未退过单
                }else{
                    if($scope.tbRefund.status==0||$scope.tbRefund.status==1)//正在处理中
                    {
                        $scope.flag=2;
                    }//退单
                    if($scope.tbRefund.status==2)//退单成功
                        $scope.flag=3;
                    if($scope.tbRefund.status==3){//失败
                        $scope.flag=4
                    }
                    $scope.status=$scope.tbRefund.status;
                    $scope.price=$scope.tbRefund.refundAmount;
                    $scope.images=$scope.tbRefund.images.split(",");
                }
            }
        )
    }
    $scope.images=[];
    //文件上传
    $scope.uploadSelected=function(){
        if($scope.images.length<=2) {
            uploadService.uploadFile().success(
                function (data) {
                    $scope.images.push(data.message);
            })
        }
    }
    //删除已上传图片
    $scope.deletePic=function (index) {
        $scope.images.splice(index,1);
    }
    //提交申请
    $scope.submit=function () {
        if( $scope.tbRefund.refundType==undefined || $scope.tbRefund.refundReason==undefined ){
            return;
        }
        if($scope.flag==2){
            refundService.cancelRefundOrder($scope.tbRefund.id).success(
                function (response) {
                    console.log(response);
                    if(response.success){
                        $scope.status=-1;
                        $scope.flag=1;//可以再次申请
                        $scope.tbRefund='';
                        $scope.images='';
                    }
                    return;
                }
            )
            return;
        }
        $scope.tbRefund.images=$scope.images.toString();
        $scope.tbRefund.refundAmount=$scope.price;
        $scope.tbRefund.outTradeNo=$scope.outTradeNo;
        $scope.tbRefund.orderItemId=$scope.orderItemId;
        $scope.tbRefund.sellerId=$scope.sellerId;
        refundService.addRefundOrder($scope.tbRefund).success(
            function (data) {
                if(data.success)//添加成功
                {
                    alert(data.message);
                    $scope.status=1;
                }
               else
                   alert(data.message);
            }
        )
    }

});