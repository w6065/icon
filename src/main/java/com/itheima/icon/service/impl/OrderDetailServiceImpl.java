package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.entity.OrderDetail;
import com.itheima.icon.mapper.OrderDetailMapper;
import com.itheima.icon.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}