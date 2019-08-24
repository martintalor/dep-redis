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

//@Configuration
public class RedissonClusterConfig {

    @Value("${spring.redis.clusters}")
    private  String cluster;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private String dataBase;

    @Bean
    public RedissonClient getRedisson(){
        Config config = new Config();
        String[] nodes = cluster.split(",");
        for(int i=0;i<nodes.length;i++){
            nodes[i] = "redis://"+nodes[i];
        }
        config.useClusterServers()
        .setScanInterval(2000) //设置集群状态扫描时间
                .addNodeAddress(nodes)
                .setPassword(password);
        config.setCodec(new org.redisson.client.codec.StringCodec());
        return Redisson.create(config);
    }

}