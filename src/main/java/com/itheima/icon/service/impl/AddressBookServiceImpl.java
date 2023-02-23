package com.itheima.icon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.icon.entity.AddressBook;
import com.itheima.icon.mapper.AddressBookMapper;
import com.itheima.icon.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
