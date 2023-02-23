package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.entity.ShoppingCart;
import com.itheima.icon.mapper.ShoppingCartMapper;
import com.itheima.icon.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
