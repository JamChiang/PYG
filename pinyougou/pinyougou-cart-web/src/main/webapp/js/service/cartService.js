app.service("cartService", function ($http) {
    this.getUsername = function () {
        return $http.get("/cart/getUsername.do?t=" + Math.random());
    };

    this.findCartList = function () {
        return $http.get("/cart/findCartList.do?t=" + Math.random());
    };

    this.addItemToCartList = function (itemId, num) {
        return $http.get("/cart/addItemToCartList.do?itemId=" + itemId + "&num=" + num);
    };

    this.sumValue = function (cartList, itemIds) {
        var totalValue = {"totalNum": 0, "sumNum": 0, "sumMoney": 0.0};

        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num;
                for (var k = 0; k < itemIds.length; k++) {
                    if (orderItem.itemId == itemIds[k]) {
                        totalValue.sumNum += orderItem.num;
                        totalValue.sumMoney += orderItem.totalFee;
                    }
                }
            }
        }
        return totalValue;
    };

    this.sumOrderValue = function (cartList) {
        var totalValue = {"totalNum": 0, "sumNum": 0, "sumMoney": 0.0};

        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValue.sumNum += orderItem.num;
                totalValue.sumMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    };

    this.submitOrder = function (order) {
        return $http.post("order/add.do", order);
    };

    this.orderAccount = function (selectedItemIds) {
        return $http.get("cart/orderAccount.do?selectedItemIds=" + selectedItemIds);
    };

    this.findOrderAccount=function () {
        return $http.get("/cart/findOrderAccount.do?t=" + Math.random());
    }
})
;