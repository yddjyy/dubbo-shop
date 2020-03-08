app.service("seckillGoodsService",function ($http) {
    this.addSeckillGoodsList=function(seckillGoodsList){
        return $http.get('../item/findItemByGoodsId?id='+seckillGoodsList);
    }
});