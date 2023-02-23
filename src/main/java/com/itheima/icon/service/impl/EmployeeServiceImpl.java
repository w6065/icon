package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.entity.Employee;
import com.itheima.icon.mapper.EmployeeMapper;
import com.itheima.icon.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
