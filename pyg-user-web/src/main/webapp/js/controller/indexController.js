//首页控制器
app.controller('indexController',function($scope,loginService,userService,$http){
	$scope.init=function(){
		$scope.showName();
	}
	$scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
						console.log($scope.loginName);
						$scope.findUserByUserName();
					}
			);
	}
	//通过用户名查找用户信息
	$scope.findUserByUserName=function(){
			userService.findUserByUserName($scope.loginName).success(
				function (response) {
					console.log(response);
					$scope.userInfo=response;
				}
			)
	}
	//保存用户修改的信息
	$scope.saveUserInfo=function(){
		userService.update($scope.userInfo.user).success(
			function (response) {
				alert(response.message);
			}
		)
	}
	$scope.showOrder = function(){
		$scope.findPage($scope.paginationConf.currentPage,
			$scope.paginationConf.itemsPerPage);
	}
	//分页控件配置
	$scope.paginationConf = {
		currentPage: 1,
		totalItems: 10,
		itemsPerPage: 6,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function () {
			$scope.reloadList();//重新加载
		}
	};
	$scope.status=['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];
    $scope.opertation=['','立即付款','提醒发货','提醒发货','查看物流','查看订单','查看订单','评价'];//交易操作
    $scope.goodsOperation=['','','退货/退款','退货/退款','退货/退款','','',''];//商品操作
    $scope.cancelOrder=['','取消订单','','','','','',''];
	//分页
	$scope.findPage = function (page, rows) {
		$http.post("../userOrders/showOrders.do?username="+$scope.loginName+"&pageNum="+$scope.paginationConf.currentPage+"&pageSize="+$scope.paginationConf.itemsPerPage).success(
			function (response) {
				$scope.list = response.rows;
				//将规格转换成json类型
				for(var i = 0; i < $scope.list.length ;i++){
					$scope.list[i].goodsSpec = JSON.parse($scope.list[i].goodsSpec);
				}
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
				$scope.findPage($scope.paginationConf.currentPage,
					$scope.paginationConf.itemsPerPage);
			}
		});
	}
});