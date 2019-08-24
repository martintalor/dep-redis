package com.iflytek.dep.server.monitor;


import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.service.dataPack.FTPService;
import com.iflytek.dep.server.service.impl.AckRetryServerScheduledImpl;
import com.iflytek.dep.server.utils.FileConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 初始化参考
 *
 * @author Kevin
 */
@Component("com.iflytek.dep.server.monitor.SpringLoadedListener")
@DependsOn("com.iflytek.dep.server.config.web.ApplicationContextRegister")
public class SpringLoadedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SpringLoadedListener.class);

    @Autowired
    private FTPService ftpService;

    @Autowired
    private AckRetryServerScheduledImpl AckReteyScheduledService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 缓存ftp信息
        List<FTPConfig> configs = ftpService.selectByServerNodeId(FileConfigUtil.SERVER_NODE_ID);
        if (!CollectionUtils.isEmpty(configs)) {
            for (FTPConfig config : configs) {
                String nodeId = config.getNodeId();
                // 缓存ftp配置信息
                FtpClientTemplate.FTP_CONFIG.put(config.getNodeId(), config);
                // 缓存FtpClientTemplate
                if (!FtpClientTemplate.FTP_CLIENT_TEMPLATE.containsKey(nodeId)) {
                    //
                    FtpClientTemplate.FTP_CLIENT_TEMPLATE.put(nodeId, new FtpClientTemplate(nodeId));
                }
                logger.info(FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(nodeId).getFtpClientConfig().toString());
            }
        }

        AckReteyScheduledService.start();
    }
}
