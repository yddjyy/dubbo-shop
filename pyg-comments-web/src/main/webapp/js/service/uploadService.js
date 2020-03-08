app.service("uploadService",function ($http) {
        this.uploadFile = function () {
            var formdata = new FormData();
            formdata.append('file',file.files[0]);
            return $http({
                url:'../upload',
                method:'post',
                data:formdata,
                headers:{'Content-Type':undefined},
                transformRequest: angular.identity
            })
        }
})