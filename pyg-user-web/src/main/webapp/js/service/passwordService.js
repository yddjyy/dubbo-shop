app.service('passwordService',function($http){

    this.changePwd=function (oldPwd,newPwd) {
      return   $http.get("../user/changePwd.do?oldPwd="+oldPwd+"&newPwd="+newPwd);
    }
});