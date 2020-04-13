app.controller('commentsController', function ($scope, commentsService,$location,uploadService,$location) {
    //初始化方法
    $scope.loadPage = function () {
        //得到跳转商品id
      // $scope.itemid= $location.search()['itemid'];
       //console.log("商品id为:"+$scope.itemid);
        //$scope.getGoods($scope.itemid);
    };
    //获取商品
    $scope.getGoods=function (itemid) {
        commentsService.getGoods(itemid).success(function(data){
            //得到商品信息
        })
    }
    //商品集合体
    $scope.commentsBody={
        orderid:'12345678977',
        spuid:'149187842867991',
        userid:'456123',
        nickname:'小白',
        parentid:'1142694948219392000',
        isparent:true,
        images:[],
        type:'追加评论'
    }
    //提交评论
    $scope.add=function(){
        console.log($scope.commentsBody);
        commentsService.add($scope.commentsBody).success(function(data){
            console.log(data);
        })
    }

    //文件上传
    $scope.uploadSelected=function(){
        if($scope.commentsBody.images.length<=4) {
            uploadService.uploadFile().success(function (data) {
                $scope.commentsBody.images.push(data.message);

            })
        }
    }
    //删除已上传图片
    $scope.deletePic=function (index) {
        alert(index);
        $scope.commentsBody.images.splice(index,1);
    }

});