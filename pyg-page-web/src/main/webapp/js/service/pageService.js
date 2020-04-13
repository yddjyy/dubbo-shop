app.service('pageService', function ($http) {
    //根据ID查询详情
    this.getPage = function (goodsId) {
        return $http.get('page/getPage?goodsId=' + goodsId);
    }
    this.addToCart = function (sku,num) {
        // alert(sku+"--"+num);
        return $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+sku+"&num=" + num,{'withCredentials':true}).success(
            function(response){
                if(response.success){
                    location.href='http://localhost:9107/cart.html';
                }else{
                    alert(response.message);
                }
            }
        );
    }
    this.getCommentsInfo=function (spuid,currentPage,PageSize) {
        return $http.get("../page/getCommentsInfo?spuid="+spuid+"&currentPage="+currentPage+"&pageSize="+PageSize);
    }
})
/**
 * http://localhost:9100/cas/login?service=http%3A%2F%2Flocalhost%3A9106%2Flogin%2Fcas
 * http://localhost:8086/
 * http://localhost:8081/shoplogin.html
 * http://localhost:8080/login.html
 **/