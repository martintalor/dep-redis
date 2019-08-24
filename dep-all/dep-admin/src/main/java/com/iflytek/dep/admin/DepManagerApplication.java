package com.iflytek.dep.admin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableRetry
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages={"com.iflytek.dep"})
public class DepManagerApplication {
    public static void main(String[] args) {
        //System.out.println(Md5Util.md5("iflytek"));

        SpringApplication.run(DepManagerApplication.class, args);
    }
}
