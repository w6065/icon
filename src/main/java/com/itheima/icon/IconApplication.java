package com.itheima.icon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching  //开启Spring Cache注解方式缓存功能
public class IconApplication {

    public static void main(String[] args) {
        SpringApplication.run(IconApplication.class, args);
        log.info("项目启动成功！~~~");
    }

}
