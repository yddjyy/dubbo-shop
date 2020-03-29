app.controller('passwordController',function($scope,loginService,passwordService){

    $scope.init=function(){
        $scope.showName();
    }
    //显示当前用户名
    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

    //点击修改密码
    $scope.flag=true;
    $scope.entity={
        newPwd:'',
        ReNewPwd:'',
        oldpwd:''
    }
    $scope.finish=0;//是否完成
    $scope.changePwd=function () {
        if($scope.entity.ReNewPwd!=$scope.entity.newPwd)//两次密码不一样
        {
            $scope.flag=false;
            $scope.message='两次输入密码不一致';
            return;
        }else {
            $scope.flag=true;
            $scope.message='';
        }
        if($scope.entity.newPwd==''){
            $scope.flag=false;
            $scope.message='密码不能为空';
            return;
        }else{
            $scope.flag=true;
            $scope.message='';
        }
        if($scope.entity.oldpwd==''){
            $scope.flag=false;
            $scope.message='密码不能为空';
            return;
        }else {
            $scope.flag=true;
            $scope.message='';
        }
        if($scope.entity.oldpwd==$scope.entity.newPwd){
            $scope.flag=false;
            $scope.message='新密码不能是曾经使用过的密码';
            return;
        }else {
            $scope.flag=true;
            $scope.message='';
        }
        passwordService.changePwd($scope.entity.oldpwd,$scope.entity.newPwd).success(
            function (response) {
                alert(response.message);
                if(response.success==true){
                    $scope.finish=1;
                    location.href="../logout/cas";
                }
                $scope.entity='';
            }
        )
    }

});