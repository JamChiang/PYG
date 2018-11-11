app.controller("searchController", function ($scope, $location, searchService) {
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            buildPageInfo();
        });
    };

    //设置搜索对象
    $scope.searchMap = {
        "keywords": "", "category": "", "brand": "", "spec": {}, "price": "",
        "pageNo": 1, "pageSize": 40, 'sortField': '', 'sort': ''
    };
    $scope.addSearchItem = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.searchMap.pageNo = 1;
        //选择后重新搜索
        $scope.search();
    };

    //删除搜索选项
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo = 1;
        //选择后重新搜索
        $scope.search();
    };

    //排序选项
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    };

    buildPageInfo = function () {
        //在页面显示的页号的集合
        $scope.pageNoList = [];

        //在页面显示的页号数量
        var showPageNoNum = 5;

        //起始页
        var startPageNo = 1;

        //结束页
        var endPageNo = $scope.resultMap.totalPages;

        //如果总页数大于显示页数才需要进行显示页处理,不然直接显示
        if (endPageNo > showPageNoNum) {

            //计算当前页左右间隔
            var interval = Math.floor(showPageNoNum / 2);

            //根据间隔计算起始和结束页
            startPageNo = parseInt($scope.searchMap.pageNo) - interval;
            endPageNo = parseInt($scope.searchMap.pageNo) + interval;

            //如果起始页出现越界
            if (startPageNo > 0) {
                //如果结束页出现越界
                if (endPageNo > $scope.resultMap.totalPages) {
                    endPageNo = $scope.resultMap.totalPages;
                    startPageNo = endPageNo - (showPageNoNum - 1);
                }

            } else {
                startPageNo = 1;
                endPageNo = showPageNoNum;
            }
        }


        //分页导航条上的前后三个点
        $scope.frontDot = false;
        $scope.backDot = false;

        if (startPageNo > 1) {
            $scope.frontDot = true;
        }
        if (endPageNo < $scope.resultMap.totalPages) {
            $scope.backDot = true;
        }

        //设置要显示的页号
        for (var i = startPageNo; i <= endPageNo; i++) {
            $scope.pageNoList.push(i);
        }
    };

    //判断是否是当前页,用于分页导航条的样式
    $scope.isCurrentPage = function (pageNo) {
        //是否需要转换格式?
        var temp = parseInt($scope.searchMap.pageNo);
        return temp == pageNo;
    };

    //根据页号查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo > 0 && pageNo <= $scope.resultMap.totalPages) {
            $scope.searchMap.pageNo = pageNo;
            $scope.search();
        }
    }

    //获取从首页传送过来的keywords
    $scope.getKeywords=function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    }
});