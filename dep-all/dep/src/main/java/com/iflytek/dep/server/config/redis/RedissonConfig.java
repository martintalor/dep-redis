package com.iflytek.dep.server.config.redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @program:
* @description: RedissonConfig
* @author: dzr
* @create: 2019-05-29
*/

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private String dataBase;

    @Bean
    public RedissonClient getRedisson(){

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);

        config.useSingleServer().setDatabase(Integer.valueOf(dataBase));
        config.setCodec(new org.redisson.client.codec.StringCodec());

        System.out.println( "==========>>>>>>>>>database:" + config.useSingleServer().getDatabase());
        return Redisson.create(config);
    }

}