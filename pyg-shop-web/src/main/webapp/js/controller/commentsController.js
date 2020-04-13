app.controller('commentsController', function ($scope,commentsService) {
    $scope.page={
        currentPage:1,
        pageSize:15,
        total:0
    }
    $scope.init=function () {
        console.log("初始化");
        $scope.findAllByPage($scope.page.currentPage,$scope.page.pageSize);
    }

    $scope.findAllByPage=function (currentPage,pageSize) {
        console.log(currentPage);
        commentsService.findAllByPage(currentPage,pageSize).success(
            function (response) {
                console.log(response);
                $scope.commentsList=response.rows;
                $scope.page.total=Math.ceil(response.total/$scope.page.pageSize);
                builPageLable();
            }
        )
    }
    //分页搜索
    $scope.queryByPage = function (pageNum) {
        if (pageNum < 1 || pageNum > $scope.page.total) {
            return;
        }
        $scope.page.currentPage = pageNum;
        $scope.findAllByPage($scope.page.currentPage,$scope.page.pageSize);
    }
    //判断是否是第一页
    $scope.isTopPage = function () {
        if ($scope.page.currentPage == 1) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否是最后一页
    $scope.isEndPage = function () {
        if ($scope.page.total == $scope.page.currentPage) {
            return true;
        } else {
            return false;
        }
    }
    //评论信息
    $scope.viewComments=function (id) {
        console.log(id);
        commentsService.viewComments(id).success(
            function (response) {
                $scope.comentsItem=response;
            }
        )
    }
    //保存id
    $scope.saveId=function (id) {
       $scope.commentsId=id;
       console.log(id);
    }

    //评论回复replyComments
    $scope.replyComments=function () {
        console.log($scope.content);
        commentsService.replyComments($scope.commentsId,$scope.content).success(
            function (response) {
               if(response.success){
                   $scope.findAllByPage($scope.page.currentPage,$scope.page.pageSize);
               }else alert(response.message)
            }
        )
    }

    //构建分页栏
    builPageLable = function () {
        $scope.pageLabel = [];
        var firstPage = 1; //开始页
        var lastPage = $scope.page.total; //截至页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.page.total > 5) { //如果总页数大于5
            if ($scope.page.currentPage <= 3) { //如果当前页码小于3显示前5页
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.page.pageNum >= $scope.page.total - 2) {
                firstPage = $scope.page.total - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.page.currentPage - 2;
                lastPage = $scope.page.currentPage + 2;
            }
        } else {
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };
})