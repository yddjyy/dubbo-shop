app.controller('addressController',function($scope,loginService,addressService){

    //初始化方法
    $scope.init=function(){
        $scope.showName();
        $scope.findAddressList();
        $scope.editOrAdd=1;//标志是修改还是添加 1增加 2 修改
        $scope.selectProvinceList();
        $scope.addressEntity='';//新增获取修改的实体类
    }
    //显示当前用户名
    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }
    $scope.address='';
    //获取当前用户的地址列表
    $scope.findAddressList=function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].address.isDefault=='1'){
                        $scope.address=$scope.addressList[i].address;
                        break;
                    }
                }
            }
        );
    }

    //选择地址
    $scope.selectAddress=function(address){
        if( $scope.address==''||$scope.address.id==undefined){//如果没有设置默认值
            $scope.preAddressId=0;
        }else{
            $scope.preAddressId=$scope.address.id;
        }
        $scope.address=address;
        $scope.nowAddress=address.id;
        if($scope.preAddressId==$scope.nowAddress){
            return ;
        }
        addressService.updateDefaultAddress($scope.preAddressId,$scope.nowAddress).success(
            function (response) {
                console.log(response);
            }
        )
    }

    //判断某地址对象是不是当前选择的地址
    $scope.isSeletedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    //删除地址信息
    $scope.delete=function (addressId) {
        addressService.delete(addressId).success(
            function (response) {
                if(response.success){
                    $scope.findAddressList();//刷新地址信息
                }
            }
        )
    }

    // //根据id查找省1 市2 县3
    // $scope.findAddressInfoById=function (id,flag) {
    //        return addressService.findAddressInfoById(id,flag);
    // }

    //修改地址信息
    $scope.edit=function (address) {
        $scope.addressEntity=address;
        $scope.editOrAdd=2;
    }

    //查找省份
    $scope.selectProvinceList=function () {
        addressService.selectProvinceList().success(
            function (response) {
                $scope.provinceList=response;
            }
        )
    }
    //根据省份查找市
    $scope.$watch('addressEntity.provinceId', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        addressService.findByProvinceId(newValue).success(
            function (data) {
                $scope.cityList=data;
            }
        )
    })
    //根据市查找县或者区
    $scope.$watch('addressEntity.cityId', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        addressService.findByCityId(newValue).success(
            function (data) {
                $scope.townList = data;
            }
        )
    });

    $scope.addressEntity='';
    //增加或修改地址
    $scope.save=function () {
        if($scope.addressEntity.id==undefined || $scope.addressEntity==''){//增加
            addressService.add($scope.addressEntity).success(
                function (response) {
                    alert(response.message);
                    $scope.cancel();//清理
                    $scope.findAddressList();//刷新地址列表
                }
            )
        }else{//修改
            addressService.update($scope.addressEntity).success(
                function (response) {
                    alert(response.message);
                    $scope.cancel();
                }
            )
        }

    }
    //点击取消按钮
    $scope.cancel=function () {
        $scope.addressEntity='';
        $scope.editOrAdd=1;
        $scope.cityList='';
        $scope.townList='';
    }
});