app.controller("brandController", function ($scope, $controller, brandService) {

    //继承baseController
    $controller("baseController",{$scope: $scope})

    // 查询所有列表数据并绑定到list对象
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.reloadList();
        });
    };

    //分页查询
    // $scope.findPage = function (page, rows) {
    //     $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows).success(function (response) {
    //         //更新记录列表 （点击哪页返回哪页数据）
    //         $scope.list = response.rows;
    //         //更新总页数
    //         $scope.paginationConf.totalItems = response.total;
    //     });
    // };

    //新建品牌
    $scope.save = function () {
        var method = "add";
        if ($scope.entity.id != null) {
            method = "update";
        }

        brandService.save(method, $scope.entity).success(function (response) {
            if (response.success) {
                $scope.reloadList();
                //提交后清除数据,或可在按钮点击时候处理 ng-click="entity={}
                //$scope.entity={};
            } else {
                alert(response.message);
            }
        });
    };

    //查找一个品牌数据
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };

    $scope.delete = function () {
        alert($scope.selectedIds)
        if ($scope.selectedIds.length < 1) {
            alert("请选择删除项!");
            return;
        }
        if (confirm("确定要进行删除吗?")) {
            brandService.delete($scope.selectedIds).success(function (response) {
                if (response.success) {
                    $scope.reloadList();

                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }

            });
        }
    };

    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity)
            .success(function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            });
    };
});

