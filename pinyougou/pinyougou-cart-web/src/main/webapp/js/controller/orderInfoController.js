app.controller("orderInfoController", function ($scope, cartService, addressService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        })
    };

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            $scope.totalValue = cartService.sumValue($scope.cartList)
        })
    };

    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success) {
                if ($scope.order.paymentType == '1') {
                    location.href = "pay.html#?outTradeNo=" + response.message;
                } else {
                    location.href = "paysuccess.html";
                }
            } else {
                alert(response.message);
            }
        });
    };


    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for (var i = 0; i < response.length; i++) {
                var address = response[i];
                if (address.isDefault == '1') {
                    $scope.address = address;
                    break;
                }
            }
        });
    };

    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    $scope.isAddressSelected = function (address) {
        return $scope.address === address;
    };

    $scope.order = {"paymentType": "1"};
    $scope.selectPayType = function (select) {
        $scope.order.paymentType = select;
    };
});