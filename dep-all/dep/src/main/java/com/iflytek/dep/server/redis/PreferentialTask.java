package com.iflytek.dep.server.redis;

import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.redis
 * @Description:
 * @date 2019/6/4--15:36
 */
@Component
public class PreferentialTask {

    @Autowired
    private RedisUtil redisUtil;

    private Logger logger = LoggerFactory.getLogger(PreferentialTask.class);

    public String getTask(String keyName) {
        String resultJson = "";

        for(LevelEnum key:LevelEnum.values()) {
            System.out.println("key:" + key);
            try {
                // downloadTaskList-pkg + 0 …… downloadTaskList-pkg + 9
                // uploadTaskList-pkg + 0 …… uploadTaskList-pkg + 9
                String level = keyName + key.toString();
                resultJson = redisUtil.rpop( level );
                if ( resultJson != null) {
                    break;
                }

                logger.info( level + " 级别没有，获取下一级别任务");

            } catch (Exception e) {
                logger.error( " [{}] has [{}]",this.getClass(), e);
            }
        }

        return resultJson;

    }

    public void setTask(String keyName, String value) {
        String resultJson = "";

        for(LevelEnum key:LevelEnum.values()) {
            try {
                // downloadTaskList-pkg + 0 …… downloadTaskList-pkg + 9
                redisUtil.lpush( keyName, value );

            } catch (Exception e) {
                logger.error( " [{}] has [{}]",this.getClass(), e);
            }
        }

    }



}
