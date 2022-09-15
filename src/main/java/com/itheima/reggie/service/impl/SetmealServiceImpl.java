package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal，执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除套餐，同时删除套餐和和菜品的关联数据
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count >0){
            //如果不能删除，抛出一个异常
            throw new CustomException("套餐正在售卖中，不能删除！");
        }
        //如果可以删除，先删除套餐表中的数据-setmeal
        this.removeByIds(ids);

        //删除关系表中的数据-setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 根据id查询套餐和菜品信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐信息，从setmeal表查
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询菜品信息，从setmeal_dish表查
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 更新菜品信息
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表基本信息
        this.updateById(setmealDto);

        //清理当前菜品数据，delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //添加当前提交过来的菜品数据，insert操作
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }


}
