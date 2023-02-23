package com.itheima.icon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.icon.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
