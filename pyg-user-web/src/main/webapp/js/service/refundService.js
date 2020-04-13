app.service('refundService',function($http){

    //通过订单id获取订单详细信息
    this.findOrderDetailInfoByOrderItemId=function (orderItemId) {
        return $http.get("../userOrders/findOrderDetailInfoByOrderItemId.do?orderItemId=" + orderItemId);
    }

    //从退款列表中查看是否有当前订单的退款信息
    this.findRefundOrderByOrderItemId=function (orderItemId) {
        return $http.get("../userOrders/findRefundOrderByOrderItemId.do?orderItemId="+orderItemId);
    }

    //添加申请
    this.addRefundOrder=function (entity) {
        return $http.post("../userOrders/addRefundOrder.do",entity);
    }
    //取消申请
    this.cancelRefundOrder=function (id) {
        return $http.get("../userOrders/cancelRefundOrder.do?id="+id);
    }

    //获取退款订单
    this.findAllRefundOrder=function () {
        return $http.get("../userOrders/findAllRefundOrderByUsername.do");
    }

});