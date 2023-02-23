package com.itheima.icon.dto;

import com.itheima.icon.entity.Setmeal;
import com.itheima.icon.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
