app.controller("indexController", function ($scope, userService, $http) {

    $scope.getUsername = function () {
        userService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addItemToCartList.do?itemId=1369369&num=1", {"withCredentials": true})
            .success(function (response) {
                if (response.success) {
                    location.href = "http://cart.pinyougou.com";
                } else {
                    alert(response.message);
                }
            });
    };
});