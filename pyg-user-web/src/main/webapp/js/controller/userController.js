 //控制层 
app.controller('userController' ,function($scope,$controller ,userService){

	$scope.entity={
		password :'',
		password2:''
	};
	//注册用户
	$scope.reg=function() {
		if($scope.entity.username==''||$scope.entity.username=='undefined'){
			alert("请输入用户名")
			return;
		}
		//
		//比较两次输入的密码是否一致
		if ($scope.entity.password2 == ""||$scope.entity.password2 == 'undefined') {
			alert("密码不能为空！请重新填写！");
			return;
		} else {
			if ($scope.entity.password2 != $scope.entity.password) {
				alert("两次输入密码不一致，请重新输入");
				$scope.entity.password = "";
				$scope.entity.password2 = "";
				return;
			}
		}
		//查看当前用户名是否已经存在
		userService.findUserByUserName($scope.entity.username).success(
			function (response) {
				if(!response.flag){
					//新增
					userService.add($scope.entity,$scope.smscode).success(
						function(response){
							if(response.success){
								if(confirm("注册成功！"))
								location.href="http://192.168.73.2:9106";
							}
							console.log(response);
						}
					);
				}else {
					alert("用户名已存在");
				}
			}
		)
		//新增
		// userService.add($scope.entity,$scope.smscode).success(
		// 	function(response){
		// 		alert(response.message);
		// 	}
		// );
	}
    
	//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请填写手机号码");
			return ;
		}
		
		userService.sendCode($scope.entity.phone ).success(
			function(response){
				alert(response.message);
			}
		);		
	}
	
});	
