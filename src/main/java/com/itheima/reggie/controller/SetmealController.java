package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        //清理所有菜品缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);
        return R.success("新增套餐成功！");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name 模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件,根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list= records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if ( category !=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除,批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        //清理所有菜品缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);
        return R.success("删除成功！");

    }

    /**
     * 起售，停售,批量操作
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(Long[] ids,@PathVariable int status){
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
            //清理所有菜品缓存
            Set keys = redisTemplate.keys("setmeal_*");
            redisTemplate.delete(keys);
        }

        return R.success("状态码成功修改！");
    }



    /**
     * 套餐详情
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 保存套餐
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        //清理所有菜品缓存
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);
        return R.success("保存菜品成功");
    }


    /**
     * 根据条件查询对应的套餐数据
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        List<SetmealDto> setmealDtoList = null;

        String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();

        //先从redis中获取缓存数据
        setmealDtoList = (List<SetmealDto>) redisTemplate.opsForValue().get(key);
        if (setmealDtoList != null){
            //如果存在，直接返回，无需查数据库
            return R.success(setmealDtoList);
        }

        //构造查询条件对象
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //添加查询状态为1（起售）的菜品
        queryWrapper.eq(Setmeal::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Setmeal::getPrice).orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        setmealDtoList = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long setmealId = item.getId();
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishList);
            return setmealDto;
        }).collect(Collectors.toList());


        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,setmealDtoList,10, TimeUnit.MINUTES);

        return R.success(setmealDtoList);
    }
}
