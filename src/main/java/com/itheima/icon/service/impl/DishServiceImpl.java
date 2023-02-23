package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.common.CustomException;
import com.itheima.icon.dto.DishDto;
import com.itheima.icon.entity.Dish;
import com.itheima.icon.entity.DishFlavor;
import com.itheima.icon.mapper.DishMapper;
import com.itheima.icon.service.DishFlavorService;
import com.itheima.icon.service.DishService;
import com.itheima.icon.service.SetmealDishService;
import com.itheima.icon.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表 dish
        this.save(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */

    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前口味数据 delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据 insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeDish(List<Long> ids) {
        //查询菜品状态，确定能否可用删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除，抛出一个异常
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        //如果可以删除
        this.removeByIds(ids);


    }


}
