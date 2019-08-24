package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.server.service.dataPack
 * @Description:
 * @date 2019/6/24--18:54
 */
@Service
public class NodeAppService {

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;


    public String getNodeId(String appId) {
        String nodeId = null;
        NodeAppBean appBean = nodeAppBeanMapper.selectByPrimaryKey(appId);
        if (null != appBean) {
            nodeId = appBean.getNodeId();
        }
        return nodeId;
    }

}