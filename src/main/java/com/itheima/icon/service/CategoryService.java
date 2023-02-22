package com.itheima.icon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.icon.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
