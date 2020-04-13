app.controller('userCommentsController',function($scope,loginService,$location,commentsService,uploadService) {

    $scope.init = function () {
        $scope.showName();
        $scope.getOrderId();//获取地址栏信息
    }

    //查询当前用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }
    //获取订单id和订单状态
    $scope.getOrderId=function () {
        $scope.orderItemid= $location.search()['orderItemid'];
        getOrderInfoByOrderItemId($scope.orderItemid);

    }

    //发布评论
    $scope.publisComments=function () {
        $scope.commentsBody.nickname=$scope.loginName;
        $scope.commentsBody.orderitemid=$scope.orderItemid;
        $scope.commentsBody.rating=$scope.remark;
        $scope.commentsBody.commentscontent=$scope.commentscontent;
        commentsService.add($scope.commentsBody).success(function(data){
            if(data.success){
                alert("评论成功!");
                location.href="http://localhost:9106/person/order.html";
            }
        })
    }
    //通过订单id得到订单信息
    getOrderInfoByOrderItemId=function (orderItemid) {
        commentsService.getOrderInfoByOrderItemId(orderItemid).success(
            function (response) {
               $scope.goods=response;
            }
        )
    }

    //商品集合体
    $scope.commentsBody={
        isparent:0,
        images:[],
    }

    //文件上传
    $scope.uploadSelected=function(){
        if($scope.commentsBody.images.length<=5) {
            uploadService.uploadFile().success(function (data) {
                console.log(data);
                $scope.commentsBody.images.push(data.message);

            })
        }
    }
    //删除已上传图片
    $scope.deletePic=function (index) {
        alert(index);
        $scope.commentsBody.images.splice(index,1);
    }
})