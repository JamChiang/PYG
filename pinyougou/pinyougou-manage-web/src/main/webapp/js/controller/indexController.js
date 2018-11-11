app.controller("indexController", function ($scope, loginService) {
    //获取登录用户名

    $scope.username = "";
    $scope.getUsername = function () {
        loginService.getUsername().success(function (response) {
            $scope.username = response.username;
        })
    };
});