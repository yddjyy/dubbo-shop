//服务层
app.service('userService',function($http){

	//查询实体
	this.findUserByUserName=function(username){
		return $http.get('../user/findUserByUserName.do?username='+username);
	}
	//增加 
	this.add=function(entity,smscode){
		return  $http.post('../user/add.do?smscode='+smscode,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../user/delete.do?ids='+ids);
	}
	//发送验证码
	this.sendCode=function(phone){
		return $http.get('../user/sendCode.do?phone='+phone);
	}
	
});
