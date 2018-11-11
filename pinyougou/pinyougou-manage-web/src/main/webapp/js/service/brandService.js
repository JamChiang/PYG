app.service("brandService", function ($http) {

    //查询所有
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };

    //新增和修改
    this.save = function (method, entity) {
        return $http.post("../brand/" + method + ".do", entity);
    };

    //查找一个数据
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };

    //删除
    this.delete = function (selectedIds) {
        return $http.get("../brand/delete.do?ids=" + selectedIds)
    };

    //查找
    this.search = function (page, rows, searchEntity) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, searchEntity)
    };

    //查找品牌列表
    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do?");
    }
});