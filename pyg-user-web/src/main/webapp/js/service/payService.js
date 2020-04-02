app.service('payService',function($http){
	//本地支付
	this.createNative=function(outTradeNo,totalFee){
		return $http.get('../pay/createNative.do?outTradeNo='+outTradeNo+"&totalFee="+totalFee);
	}
	
	//查询支付状态
	this.queryPayStatus=function(out_trade_no){
		return $http.get('../pay/queryPayStatus.do?out_trade_no='+out_trade_no);
	}
});