package com.itheima.icon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.icon.common.BaseContext;
import com.itheima.icon.common.R;
import com.itheima.icon.dto.OrdersDto;
import com.itheima.icon.entity.OrderDetail;
import com.itheima.icon.entity.Orders;
import com.itheima.icon.service.OrderDetailService;
import com.itheima.icon.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        orderService.submit(orders);
        return R.success("下单成功！");
    }

    @GetMapping("/userPage")
    public R<Page> getOrders(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>();
        // 1.得到用户id 因为通过id来查询我们需要的内容
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 2.根据用户id分页查询出该用户的订单信息
        queryWrapper.eq(userId != null, Orders::getUserId, userId)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper);

        // 3.复制page 给我们dtopage 除了里面的数据
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        // 将查出来的订单信息 根据orderid查出订单对应的菜品信息 从新转成list
        List<OrdersDto> ordersDtos = pageInfo.getRecords().stream().map((item) -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item, dto);
            // 查询我们的订单明细
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(item.getId() != null, OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).collect(Collectors.toList());

        // 从新返回结果集
        dtoPage.setRecords(ordersDtos);
        return R.success(dtoPage);
    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(number != null, Orders::getNumber, number)
                .between(beginTime != null || endTime != null, Orders::getOrderTime, beginTime, endTime)
                .orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);

    }



    @PutMapping
    public R<String> send(@RequestBody Orders orders){
        Long id = orders.getId();
        Integer status = orders.getStatus();
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,id);
        Orders one = orderService.getOne(queryWrapper);
        one.setStatus(status);
        orderService.updateById(one);
        return R.success("派送成功");
    }


    /**
     * 再来一单
     * 目的是直接跳转到首页，避免误点直接下单成功。
     * 注释的是直接下单成功，并且跳转到首页。
     * @param order1
     * @return
     */
    @Transactional
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders order1){
//        //取得orderId
//        Long id = order1.getId();
//        Orders orders = orderService.getById(id);
//        //设置订单号码
//        long orderId = IdWorker.getId();
//        orders.setId(orderId);
//        //设置订单号码
//        String number = String.valueOf(IdWorker.getId());
//        orders.setNumber(number);
//        //设置下单时间
//        orders.setOrderTime(LocalDateTime.now());
//        orders.setCheckoutTime(LocalDateTime.now());
//        orders.setStatus(2);
//        //向订单表中插入一条数据
//        orderService.save(orders);
//        //修改订单明细表
//        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(OrderDetail::getOrderId,id);
//        List<OrderDetail> list = orderDetailService.list(queryWrapper);
//        list.stream().map((item)->{
//            //订单明细表id
//            long detailId = IdWorker.getId();
//            //设置订单号码
//            item.setOrderId(orderId);
//            item.setId(detailId);
//            return item;
//        }).collect(Collectors.toList());
//
//        //向订单明细表中插入多条数据
//        orderDetailService.saveBatch(list);
        return R.success("再来一单");
    }
}
