package com.iflytek.dep.server;

import com.iflytek.dep.server.utils.DataInit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableRetry
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.iflytek.dep"})
@MapperScan(basePackages = "com.iflytek.dep.server.mapper")
public class DepApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DepApplication.class);
//		app.addListeners(new ContextStartListener());
		app.run(args);
	}

	@Bean
	DataInit dataInit() {
		return new DataInit();
	}
}
