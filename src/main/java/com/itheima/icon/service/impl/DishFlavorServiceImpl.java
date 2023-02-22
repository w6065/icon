package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.entity.DishFlavor;
import com.itheima.icon.mapper.DishFlavorMapper;
import com.itheima.icon.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
