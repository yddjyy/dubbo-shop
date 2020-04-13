app.service('commentsService',function($http){
    this.findAllByPage=function (currentpage,pagesize) {
        return $http.get("../comments/findBysellerid?currentPage="+currentpage+"&pageSize="+pagesize);
    }
    this.viewComments=function (id) {
        return $http.get("../comments/find?id="+id);
    }
    this.replyComments=function (id,content) {
        return $http.get("../comments/update?id="+id+"&content="+content);
    }
})