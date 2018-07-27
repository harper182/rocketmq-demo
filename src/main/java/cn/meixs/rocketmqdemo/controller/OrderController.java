package cn.meixs.rocketmqdemo.controller;

import cn.meixs.rocketmqdemo.order.application.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation("/pay")
    @GetMapping("/pay")
    public boolean getUser(){
        orderService.pay("1111", new BigDecimal("100"));
        return true;
    }
}
