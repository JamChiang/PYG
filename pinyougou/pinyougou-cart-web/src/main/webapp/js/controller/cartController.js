app.controller("cartController", function ($scope, cartService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        })
    };

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.selectedItemIds = [];
            $scope.cartList = response;
            $scope.totalValue = cartService.sumValue($scope.cartList, $scope.selectedItemIds)
        })
    };

    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId, num).success(function (response) {
            if (response.success) {
                $scope.findCartList();
            } else {
                alert(response.message);
            }
        })
    };


    //商品多选
    $scope.updateChooseItemList = function ($event, itemId, cart) {
        if ($event.target.checked) {
            $scope.selectedItemIds.push(itemId);
        } else {
            var index = $scope.selectedItemIds.indexOf(itemId);
            $scope.selectedItemIds.splice(index, 1);
        }

        if ($scope.selectedItemIds.length < 1) {
            cart.check = false;
        } else {
            cart.check = true;
        }
        $scope.totalValue = cartService.sumValue($scope.cartList, $scope.selectedItemIds)
    };

    //某个商家全选
    $scope.sellerAll = function (cart) {
        for (var i = 0; i < cart.orderItemList.length; i++) {
            var orderItem = cart.orderItemList[i];
            if (cart.check == true) {
                $scope.selectedItemIds.push(orderItem.itemId);
                orderItem.check = true;
            } else {
                var index = $scope.selectedItemIds.indexOf(orderItem.itemId);
                $scope.selectedItemIds.splice(index, 1);
                orderItem.check = false;
            }
        }
        $scope.totalValue = cartService.sumValue($scope.cartList, $scope.selectedItemIds)
    };

    //购物车全选
    $scope.selectAll = function (checkAll) {
        for (var i = 0; i < $scope.cartList.length; i++) {
            var cart = $scope.cartList[i];
            cart.check = (!!checkAll);
            $scope.sellerAll(cart);
        }

    };

    $scope.test = function () {
        alert($scope.selectedItemIds);
    }


})
;