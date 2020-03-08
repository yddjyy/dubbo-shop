app.service("brandService", function ($http) {
    this.findAll = function () {
        return $http.get("../brand/findAll");
    };
    this.add = function (entity) {
        return $http.post('../brand/add', entity);
    };
    this.update = function (entity) {
        return $http.post('../brand/update', entity);
    };
    this.findOne = function (id) {
        return $http.get('../brand/findOne?id=' + id);
    };
    this.delete = function (ids) {
        return  $http.get('../brand/delete?ids=' + ids);
    };
    this.search = function (entity,page,rows) {
        return $http.post('../brand/search?page=' + page + '&rows=' + rows, entity);
    };
    this.selectOptionList =function () {
        return $http.get('../brand/selectOptionList');

    }
});