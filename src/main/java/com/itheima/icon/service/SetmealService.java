package com.itheima.icon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.icon.dto.SetmealDto;
import com.itheima.icon.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);


    /**
     * 根据id查询套餐信息和对应的菜品信息
     */
    public SetmealDto getByIdWithDish(Long id);

    /**
     * 更新菜品
     */
    public void updateWithDish(SetmealDto setmealDto);




}
