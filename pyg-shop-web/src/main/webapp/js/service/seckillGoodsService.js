app.service("seckillService",function ($http) {
    this.getStartTimeOfDay = function (startDate) {
        return $http.get('../seckilltime/getStartTimeOfDay?startDate='+startDate);
    }
    //增加秒杀商品
    this.addSeckillGoods = function (entity) {
        return $http.post('../seckillGoods/add', entity);
    }
});