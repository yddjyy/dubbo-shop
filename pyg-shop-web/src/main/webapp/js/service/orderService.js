app.service('orderService',function($http){

    this.findAllByPage=function (currentpage,pagesize) {
        return $http.get("../order/findAllByPage?currentPage="+currentpage+"&pageSize="+pagesize);
    }
    //点击发货
    this.sendGoods=function(orderId,shippingCode){
        return $http.get("../order/sendGoods?orderId="+orderId+"&shippingCode="+shippingCode);
    }
    //点击查看订单商品信息
    this.viewGoods=function(orderId){
        return $http.get("../order/viewGoods?orderId="+orderId);
    }

    //获取退款订单
    this.findRefundOrderBySellerId=function () {
        return $http.get("../order/findRefundOrderBySellerId");
    }
    //查看退款信息
    this.viewRefundOrder=function (refundId) {
        return $http.get("../order/viewRefundOrder?refundId="+refundId);
    }
    //拒接退款
    this.returnRefund=function (refundId,reply) {
        return $http.get("../order/returnRefund?refundId="+refundId+"&reply="+reply);
    }
    //同意退款
    this.returnOk=function (refundId,refund_amount,outTradeNo) {
        return $http.get("../order/refundOk?refundId="+refundId+"&refundAmount="+refund_amount+"&outTradeNo="+outTradeNo);
    }

    //查看历史订单
    this.findHistoryByPage=function (currentPage,pageSize) {
        return $http.get("../order/findHistoryByPage?currentPage="+currentPage+"&pageSize="+pageSize);
    }
});