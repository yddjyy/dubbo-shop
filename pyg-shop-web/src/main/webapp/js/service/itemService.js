app.service('itemService',function($http){
    this.findItemByGoodsId=function(id){
        return $http.get('../item/findItemByGoodsId?id='+id);
    }
});