app.service('contentService', function ($http) {
    //根据广告分类ID查询广告
    this.findByCategoryId = function (categoryId) {
        return $http.get('../content/findByCategoryId?categoryId=' + categoryId);
    }
    //加载离线推荐
    this.loadOffline=function (userid,num,model) {
        return $http.get("http://192.168.73.2:8088/rest/product/offline?username="+userid+"&num="+num+"&model="+model);
    }
    //加载实时推荐
    this.loadStream=function (userid,num,model) {
        return $http.get("http://192.168.73.2:8088/rest/product/stream?username="+userid+"&num="+num+"&model="+model);
    }
    //获取热门推荐
    this.loadHot=function (num,model) {
        return $http.get("http://192.168.73.2:8088/rest/product/hot?num="+num+"&model="+model);
    }
    //获取打分最多的商品
    this.loadRate=function (num,model) {
        return $http.get("http://192.168.73.2:8088/rest/product/rate?num="+num+"&model="+model);
    }
});