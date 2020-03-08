app.service('brandService',function($http){
    this.findAll=function(){
        return $http.get('../brand/findAll');
    }
});