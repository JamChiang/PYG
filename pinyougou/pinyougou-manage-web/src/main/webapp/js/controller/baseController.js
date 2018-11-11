app.controller("baseController", function ($scope) {

    //初始化分页参数,只要渲染则会执行并调用onChange
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [5, 10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };

    //加载表格数据
    $scope.reloadList = function () {
        // $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.master = false;
        $scope.selectedIds = [];
    };

    $scope.selectedIds = [];

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            var index = $scope.selectedIds.indexOf(id);
            $scope.selectedIds.splice(index, 1);
        }

    };

    //全选
    $scope.all = function (master, list) {
        $scope.selectedIds = [];
        if (master == true) {
            // $scope.x = true;
            for (var i = 0; i < list.length; i++) {
                $scope.selectedIds.push(list[i].id);
            }
        }
    };

    //将一个 json 数组格式字符串的某个 key 对应的值串起来显示，使用,分隔
    $scope.jsonToString = function (jsonStr, key) {
        var str = "";
        var jsonArray = JSON.parse(jsonStr);

        for (var i = 0; i < jsonArray.length ; i++){
            if (i > 0) {
                str += ",";
            }
            str += jsonArray[i][key];
        }
        return str;

    };


    
});

