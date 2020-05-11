 //控制层 
app.controller('seckilltimeController' ,function($scope,$controller   ,seckilltimeService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckilltimeService.findAll().success(
			function(response){
				$scope.list=response;
				console.log($scope.list);
			}
		);
	}
	$scope.typeEntity={};
	$scope.typeEntity2={};
	$scope.typeList = {data: [
			{id: 0, text: "00"},
			{id: 1, text: "01"},
			{id: 2, text: "02"},
			{id: 3, text: "03"},
			{id: 4, text: "04"},
			{id: 5, text: "05"},
			{id: 6, text: "06"},
			{id: 7, text: "07"},
			{id: 8, text: "08"},
			{id: 9, text: "09"},
			{id: 10, text: "10"},
			{id: 11, text: "11"},
			{id: 12, text: "12"},
			{id: 13, text: "13"},
			{id: 14, text: "14"},
			{id: 15, text: "15"},
			{id: 16, text: "16"},
			{id: 17, text: "17"},
			{id: 18, text: "18"},
			{id: 19, text: "19"},
			{id: 20, text: "20"},
			{id: 21, text: "21"},
			{id: 22, text: "22"},
			{id: 23, text: "23"},
		]};
	//分页
	$scope.findPage=function(page,rows){
		seckilltimeService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		seckilltimeService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// $scope.typeEntity.text=$scope.entity.startTime;
				// $scope.typeEntity2.text=$scope.entity.endTime;
			}
		);				
	}

	$scope.entity={};
	//保存 
	$scope.save=function(){
		// $scope.entity.startTime=$scope.typeEntity.text;
		// $scope.entity.endTime=$scope.typeEntity2.text;
		var serviceObject;//服务层对象
		console.log($scope.entity);
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckilltimeService.update( $scope.entity ); //修改
		}else{
			serviceObject=seckilltimeService.add( $scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				console.log(response);
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
				$scope.entity.startTime='';
				$scope.entity.endTime='';
				$scope.entity.info='';
			}		
		);

	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		seckilltimeService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){
		seckilltimeService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
				$scope.pageChange();
			}			
		);
	}
	$scope.pageChange=function(){
		//翻页
		$(".zxf_pagediv").createPage({
			pageNum: Math.ceil($scope.paginationConf.totalItems/$scope.paginationConf.itemsPerPage),
			current: $scope.paginationConf.currentPage,
			backfun: function(e) {
				$scope.paginationConf.currentPage=e.current;
				$scope.reloadList();
			}
		});
	}
});	
