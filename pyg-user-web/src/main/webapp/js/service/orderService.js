app.service('orderService',function($http){
    this.waitPayment =function() {
      return  $http.post("../userOrders/waitPayment.do");
    }

    //通过状态查询订单信息
    this.findOrderByStatusAndPage=function (loginName,pageNum,pageSize,status) {
        return $http.get("../userOrders/showOrdersByStatus.do?username="+loginName+"&pageNum="+pageNum+"&pageSize="+pageSize+"&status="+status);
    }

    //删除订单
    this.delOrderByOrderId=function (orderId) {
        return $http.get("../userOrders/delOrderByOrderId.do?orderId="+orderId);
    }

    //取消订单
    this.cancelOrder=function (outTradeNo) {
        return $http.get("../userOrders/cancelOrder.do?outTradeNo="+outTradeNo);
    }

    //通过订单id获取订单的地址
    this.findAddressByOrderId=function (orderId) {
        return $http.get("../userOrders/findAddressByOrderId.do?orderId="+orderId);
    }

    //通过订单id获取订单详细信息
    this.findOrderDetailInfoByOrderId=function (orderId) {
        return $http.get("../userOrders/findOrderDetailInfoByOrderId.do?orderId="+orderId);
    }
});