//购物车控制层
app.controller('cartController',function($scope,cartService){
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue= cartService.sum($scope.cartList,$scope.selectIds)
			}
		);
	}
	//查询订单列表
	// $scope.findOrderList=function(){
	// 	cartService.findOrderList().success(
	// 		function(response){
	// 			$scope.cartList=response;
	// 			console.log($scope.cartList);
	// 			$scope.totalValue= cartService.sum($scope.cartList,$scope.selectIds)
	// 		}
	// 	);
	// }
	
	//数量加减
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){//如果成功
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);
				}				
			}		
		);		
	}

	$scope.praseJson=function(jsonStr){
		return  eval('(' + jsonStr + ')');
	}

	
	//获取当前用户的地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault=='1'){
						$scope.address=$scope.addressList[i];
						break;
					}					
				}
				
			}
		);		
	}
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;		
	}
	
	//判断某地址对象是不是当前选择的地址
	$scope.isSeletedAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}		
	}
	
	$scope.order={paymentType:'1'};//订单对象
	
	//选择支付类型
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder( $scope.order ).success(
			function(response){
				//alert(response.message);
				if(response.success){
					//页面跳转
					if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
						location.href="pay.html";
					}else{//如果货到付款，跳转到提示页面
						location.href="paysuccess.html";
					}
				}else{
					alert(response.message);	//也可以跳转到提示页面				
				}
				
			}				
		);		
	}
	//当刷新订单是看看是否刷新前已选中
	$scope.isSelected=function(itemId){
		if($scope.selectIds.includes(itemId)){
			return true;
		}
		return false;
	}
	//选中项
	$scope.selectIds = [];
	$scope.updateSelection = function ($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		} else {
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index, 1);
		}
		$scope.totalValue= cartService.sum($scope.cartList,$scope.selectIds);
	};

	//点击立刻下单之后，创建并向redis中存储订单信息
	$scope.createOrder=function () {
		if($scope.selectIds.length==0){
			return;
		}
		cartService.createOrde($scope.selectIds).success(
			function (response) {
				if(response.success){
					location.href="getOrderInfo.html";
				}
			}
		);
	}
	//从redis中获取预付款的商品列表列表
	$scope.findPreOrderList=function () {
		cartService.findOrderList().success(
			function (response) {
				$scope.orderList=response;
				$scope.totalFree= cartService.sumFree($scope.orderList)
			}
		);
	}
	
});