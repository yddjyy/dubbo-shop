app.service('commentsService',function($http){

    this.getOrderInfoByOrderItemId=function (orderItemid) {
       return $http.get("../userOrders/getOrderInfoByOrderItemId.do?orderItemId="+orderItemid)
    }
    this.add=function(tbComments){
        return $http.post("../comments/add.do",tbComments);
    }
})