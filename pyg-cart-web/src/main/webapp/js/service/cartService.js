//购物车服务层
app.service('cartService',function($http){
	//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	//求合计数
	this.sum=function(cartList,selectIds){
		var totalValue={totalNum:0,totalMoney:0 };
			
		for(var i=0;i<cartList.length ;i++){
			var cart=cartList[i];//购物车对象
			for(var j=0;j<cart.cartItemList.length;j++){
				var cartItem=  cart.cartItemList[j];//购物车明细
				for(var id=0;id<selectIds.length;id++){
					if(selectIds[id]==cartItem.itemId){
						totalValue.totalNum+=cartItem.num;//累加数量
						totalValue.totalMoney+=cartItem.totalFee;//累加金额
					}
				}

			}			
		}
		return totalValue;
		
	}
	
	//获取当前登录账号的收货地址
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');
	}
	
	//提交订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}

	//创建要购买的订单
	this.createOrde=function (selectIds) {
		return $http.post('cart/createOrderRedis.do',selectIds);
	}

	//从redis中获取最新的订单表
	this.findOrderList=function(){
		return $http.get('cart/findOrderList.do');
	}

	//获取订单中的总数量和总价格
	this.sumFree=function(cartList){
		var totalValue={totalNum:0,totalMoney:0 };
		for(var i=0;i<cartList.length ;i++){
			var cart=cartList[i];//购物车对象
			for(var j=0;j<cart.cartItemList.length;j++){
				var cartItem=  cart.cartItemList[j];//购物车明细
				totalValue.totalNum+=cartItem.num;//累加数量
				totalValue.totalMoney+=cartItem.totalFee;//累加金额
			}
		}
		return totalValue;
	}
	
});