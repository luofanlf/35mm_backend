package com.luofan.backend_35mm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.luofan.backend_35mm.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Backend35mmApplication {

    public static void main(String[] args) {
        SpringApplication.run(Backend35mmApplication.class, args);
    }

}
