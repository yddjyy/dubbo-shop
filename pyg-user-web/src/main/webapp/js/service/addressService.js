app.service('addressService',function($http){

    //获取当前登录账号的收货地址
    this.findAddressList=function(){
        return $http.get('../address/findListByLoginUser.do');
    }

    //更改默认地址
    this.updateDefaultAddress=function(preAddressId,nowAddresssId){
        return $http.get("../address/updateDefaultAddress.do?preAddressId="+preAddressId+"&nowAddressId="+nowAddresssId);
    }

    //删除地址信息
    this.delete=function (addressId) {
        return $http.get("../address/deleteAddressById.do?addressId="+addressId);
    }

    //根据id查找省1 市2 县3
    this.findAddressInfoById=function (id,flag) {
        return $http.get("../address/findAddressInfoById.do?id="+id+"&flag="+flag);
    }
    //查找省份
    this.selectProvinceList=function () {
        return $http.get("../address/selectProvinceList.do");
    }

    //通过省份查找市
    this.findByProvinceId=function (provinceId) {
        return $http.get("../address/findByProvinceId.do?provinceId="+provinceId);
    }

    //通过市查找县或者区
    this.findByCityId=function (cityId) {
        return $http.get("../address/findByCityId.do?cityId="+cityId);
    }

    //新增收货地址
    this.add=function (addressEntity) {
        return $http.post("../address/add.do",addressEntity);
    }

    //修改收货地址
    this.update=function (addressEntity) {
        return $http.post("../address/update.do",addressEntity);
    }
});