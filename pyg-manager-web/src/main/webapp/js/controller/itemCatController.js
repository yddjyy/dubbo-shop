 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		$scope.entity.typeId=$scope.typeEntity.id;
		$scope.entity.parentId = $scope.parentId;
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
		console.log($scope.selectIds+"---要删除的id");
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success) {
                    $scope.findByParentId($scope.parentId);//刷新列表
                }else{
					alert("删除失败");
				}
                $scope.selectIds = [];
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	

    $scope.parentId = 0;
	$scope.searchEntity.parentId=0;
	//根据上层ID查列表
	$scope.findByParentId = function (parentId) {
		// console.log(parentId+"=====================");
		$scope.parentId = parentId;
		$scope.searchEntity.parentId=parentId;
		$scope.search();
		// itemCatService.findByParentId(parentId).success(
		// 	function (data) {
		// 		$scope.list = data;
        //     }
		// )
    }
    $scope.grade = 1;
	$scope.setGrade = function (val) {
		$scope.grade = val;
    }
    $scope.selectList = function (p_entity) {
		if($scope.grade == 1){
			$scope.entity_1 = null;
            $scope.entity_2 = null;
		}else if($scope.grade == 2){
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
		}else if($scope.grade == 3){
            $scope.entity_2 = p_entity;
		}
		$scope.findByParentId(p_entity.id)
    }

    $scope.typeList = {data: []};
    $scope.selectOptionList=function () {
		typeTemplateService.selectOptionList().success(
			function (data) {
				$scope.typeList = {data:data};
            }
		)
    }
	//搜索
	$scope.search=function(page,rows){
    	// console.log($scope.searchEntity.parentId+"----------------------------");
    	if (page!=null){
			$scope.paginationConf.currentPage=page;
		}
		if (rows!=null){
			$scope.paginationConf.itemsPerPage=rows;
		}

		itemCatService.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
				// console.log($scope.list);
				$scope.pageChange();
			}
		);
	};
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
