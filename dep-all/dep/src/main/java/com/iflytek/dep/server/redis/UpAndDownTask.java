package com.iflytek.dep.server.redis;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.config.redis.DepThreadFactory;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.down.PkgGetterManger;
import com.iflytek.dep.server.service.dataPack.NodeAppService;
import com.iflytek.dep.server.service.dataPack.ParseAckService;
import com.iflytek.dep.server.up.PkgUploaderManager;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


@Component
@EnableScheduling
public class UpAndDownTask {

    private Logger logger = LoggerFactory.getLogger(UpAndDownTask.class);
    public  ThreadPoolExecutor threadPool;

    @Value("${downloadTask.thread.number}")
    public int threadNumber = 16;//Runtime.getRuntime().availableProcessors();


}