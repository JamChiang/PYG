app.controller("cartController", function ($scope, cartService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        })
    };

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            $scope.totalValue= cartService.sumValue($scope.cartList,$scope.selectedItemIds)
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

    $scope.selectedItemIds = [];

    $scope.updateChooseItemList = function ($event, itemId) {
        if ($event.target.checked) {
            $scope.selectedItemIds.push(itemId);
        } else {
            var index = $scope.selectedItemIds.indexOf(itemId);
            $scope.selectedItemIds.splice(index, 1);
        }
        if ($scope.selectedItemIds.length > 0) {
            $scope.sellerChoose = true;
        }else{
            $scope.sellerChoose = false;
        }
        $scope.totalValue= cartService.sumValue($scope.cartList,$scope.selectedItemIds)
    };

    //全选
    $scope.sellerAll = function (sellerChoose,cart) {
        $scope.selectedItemIds = [];
        // $scope.sellerChoose = sellerChoose;
        if ($scope.sellerChoose == true) {
            $scope.sellerCheck = true;
            for (var i = 0; i < cart.orderItemList.length; i++) {
                var item = cart.orderItemList[i];
                $scope.selectedItemIds.push(item.itemId);
            }
        }else{
            $scope.sellerCheck = false;
        }
        $scope.totalValue= cartService.sumValue($scope.cartList,$scope.selectedItemIds)
    };

    $scope.test=function () {
        alert($scope.selectedItemIds);
        alert($scope.sellerChoose);
    }


});