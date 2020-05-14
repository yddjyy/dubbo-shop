//控制层
app.controller('GoodsController' ,function($scope,$location,seckillGoodsService,$interval,seckilltimeService){
	//读取列表数据绑定到表单中
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				//$scope.list=response;
			}
		);
	}
	$scope.findTimeList=function(){
		seckilltimeService.findTimeList().success(
			function (response) {
				console.log("findTiemList1");
				$scope.list=response;
				console.log(response[0]);
				$scope.nowTime=response[0].startTime;
				$scope.getNowTimeGoods(response[0]);
			}
		)
	}
	$scope.flag=0;//是否可抢购
	$scope.search={};
	$scope.getGoods=function(startData,startTime){
		if($scope.nowTime!=startTime){
			$scope.flag=1;//不可抢购
		}
		$scope.search.startData=startData;
		$scope.search.startTime=startTime;
		$scope.getNowTimeGoods($scope.search);
	}
	$scope.getNowTimeGoods=function(entity){
		seckillGoodsService.getNowTimeGoods(entity).success(
			function (response) {
				console.log("得到商品");
				$scope.goods=response;
			}
		)
	}
	//查询商品
	$scope.findOne=function(){
		//接收参数ID
		var id= $location.search()['id'];
		var goodsId= $location.search()['goodsId'];
		var startDate= $location.search()['startDate'];
		var startTime= $location.search()['startTime'];
		seckillGoodsService.findOne(startDate,startTime,id).success(
			function(response){
				console.log(response);
				$scope.entity=response;
				//倒计时开始
				//获取从结束时间到当前日期的秒数
				allsecond=  Math.floor( (new Date($scope.entity.endTime).getTime()- new Date().getTime())/1000 );
				//当时间为负值时从缓存中移除
				time= $interval(function(){
					allsecond=allsecond-1;
					$scope.timeString= convertTimeString(allsecond);
					if(allsecond<=0){
						$interval.cancel(time);
						seckillGoodsService.removeGoodsFromRedis(id).success(function (response) {
							console.log(response);
						});
					}
				},1000);

			}
		);
	}
	$scope.num = 1;
	//数量加减
	$scope.addNum = function (x) {
		$scope.num += x;
		if($scope.num <= 1){
			$scope.num = 1;
		}
		if($scope.num>$scope.entity.stockCount){
			$scope.num=$scope.entity.stockCount;
		}
	}
	
	//转换秒为   天小时分钟秒格式  XXX天 10:22:33
	convertTimeString=function(allsecond){
		var days= Math.floor( allsecond/(60*60*24));//天数
		var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
		var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		var timeString="";
		if(days>0){
			timeString=days+"天 ";
		}
		return timeString+hours+":"+minutes+":"+seconds;
		
	}
	
	//提交订单 
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id ).success(
			function(response){
				if(response.success){//如果下单成功
					alert("抢购成功，请在5分钟之内完成支付");
					location.href="pay.html";//跳转到支付页面				
				}else{
					alert(response.message);
				}				
			}
		);
		
	}
	$scope.updateOrder=function(){
		seckillGoodsService.updateOrder($scope.out_trade_no).success(
			function(response){
				console.log("seckillGoodsController层");
			}
		)
	}
	//获取当前用户的地址列表
	$scope.findAddressList=function(){
		seckillGoodsService.findAddressList().success(
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
	
});