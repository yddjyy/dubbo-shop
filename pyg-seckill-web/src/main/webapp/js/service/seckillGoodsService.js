app.service('seckillGoodsService',function($http){
	//读取列表数据绑定到表单中
	this.findList=function(){
		return $http.get('seckillGoods/findList.do');
	}
	
	//根据ID查询商品
	this.findOne=function(startDate,startTime,id){
		return $http.get('seckillGoods/findOneFromRedis.do?id='+id+"&startDate="+startDate+"&startTime="+startTime);
	}
	
	
	//提交订单
	this.submitOrder=function(seckillId){
		return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
	}

	//从缓存中移除
	this.removeGoodsFromRedis=function(goodsid){
		return $http.get('seckillGoods/removeGoodsFromRedis.do?goodsId='+goodsid);
	}
	//获取当前登录账号的收货地址
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');
	}

	//获取当前秒杀商品
	this.getNowTimeGoods=function (entity) {
		return $http.post("seckillGoods/findList.do",entity);
	}

});