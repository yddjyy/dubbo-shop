app.service('commentsService', function ($http) {
   this.getGoods=function (itemid) {
      //return  $http.post();
   }
   this.add=function(tbComments){
      return $http.post("../comments/add",tbComments);
   }
});