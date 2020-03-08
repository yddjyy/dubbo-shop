//控制层
app.controller('goodsController', function ($scope,$http, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService, brandService,itemService,addSeckillGoodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //通过商品id查找
    $scope.findItemByGoodsId=function(id){
        itemService.findItemByGoodsId(id).success(
            function (response) {
                $scope.itemList=response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数

            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //描述
                editor.html(response.goodsDesc.introduction);
                //图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //sku列表
                for(var i = 0; i < $scope.entity.itemList.length ;i++){
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert("保存成功！");
                    location.href = 'goods.html'
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
    //重新提交
    $scope.resubmit=function(){
        $http.get('../goods/resubmit?ids='+ids);
    }

    $scope.searchEntity = {};//定义搜索对象
    $scope.seckillEntity={};//定义秒杀对象
    $scope.insertValue=function(id,goodsname,one,two,three,brand){
        console.log(id,goodsname,one,two,three,brand);
        $scope.seckillEntity.id=id;
        $scope.seckillEntity.goodsName=goodsname;
        $scope.seckillEntity.one=one;
        $scope.seckillEntity.two=two;
        $scope.seckillEntity.three=three;
        $scope.seckillEntity.brand=brand;
        $scope.findItemByGoodsId(id);
    }
    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
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
    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (data) {
                if (data.success) {
                    $scope.image_entity.url = data.message;
                } else {
                    alert(data.message);
                }
            }
        )
    }
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
    //向goodsdesc的itemImages字段添加图片实体
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //在itemImages字段删除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //查找一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (data) {
                $scope.itemCat1List = data;
            }
        )
    }
    //查找二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        itemCatService.findByParentId(newValue).success(
            function (data) {
                $scope.itemCat2List = data;
            }
        )
    })
    //查找三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        itemCatService.findByParentId(newValue).success(
            function (data) {
                $scope.itemCat3List = data;
            }
        )
    })

    //读取模板ID
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        itemCatService.findOne(newValue).success(
            function (data) {
                $scope.entity.goods.typeTemplateId = data.typeId;
            }
        )
    })

    //读取到模板变化后 读取品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        if(newValue==undefined){
            return;
        }
        typeTemplateService.findOne(newValue).success(
            function (data) {
                $scope.typeTemplate = data;
                //品牌列表
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //扩展属性
                if ($location.search()['id'] == null) { //如果是增加商品
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        );
        //读取规格
        typeTemplateService.findSpecList(newValue).success(

            function (data) {
                $scope.specList = data;
            }
        );

        //修改选中规格
        $scope.updateSpecAttribute = function ($event, name, value) {
            //查找规格是否存在
            var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
            //存在
            if (obj != null) {
                //判断是选中还是取消
                if ($event.target.checked) {
                    //选中push value到attributeValue集合
                    obj.attributeValue.push(value);
                } else {
                    //取消 删除attributeValue中的value值
                    obj.attributeValue.splice(obj.attributeValue.indexOf(value), 1);
                    //如果attributeValue为空了 从specificationItems移除当前规格
                    if (obj.attributeValue.length == 0) {
                        $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj), 1);
                    }
                }
            } else {
                $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]})
            }
        }
    });
    //创建sku列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//列表初始化
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }

    addColumn = function (list, columnName, columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow)); //深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    //审核状态
    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];

    $scope.itemCatList = [];
    $scope.brandList=[];
    //查询商品分类
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (data) {
                for (var i = 0; i < data.length; i++) {
                    $scope.itemCatList[data[i].id] = data[i].name;
                }
            }
        )
    }
    //查询品牌分类
    $scope.findBrandList = function () {
        brandService.findAll().success(
            function (data) {
                for (var i = 0; i < data.length; i++) {
                    //+"("+data[i].firstChar+")"
                    $scope.brandList[data[i].id] = data[i].name;
                }
            }
        )
    }

    $scope.checkedAttributeValue = function (specName, optionName) {
        var item = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(item, 'attributeName', specName);
        if (obj != null) {
            //如果选项在列表中
            if (obj.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //添加秒杀商品
    $scope.addSeckillGoodsList=function(seckillGoodsList){


    }
});
