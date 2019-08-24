package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.server.constants.GlobalState;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.PackageGlobalStateBeanMapper;
import com.iflytek.dep.server.model.DataNodeProcessBean;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.PackageGlobalStateBean;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/6/6--14:01
 */
@Service
public class AckRetryService {
    @Autowired
    PackageGlobalStateBeanMapper packageGlobalStateBeanMapper;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    DataNodeProcessBeanMapper dataNodeProcessBeanMapper;

    @Autowired
    SendAckService sendAckService;

    public void parsing(String path, String fileName) throws Exception {
        String appIdFrom = PackUtil.splitAppFrom(fileName);
        String appIdTo = PackUtil.splitAppTos(fileName);
        NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdFrom);
        NodeAppBean nodeAppBean1 = nodeAppBeanMapper.selectByPrimaryKey(appIdTo);
        //获取原来包的from和to不用反转
        String nodeId = nodeAppBean.getNodeId();
        String toNodeId = nodeAppBean1.getNodeId();
        //构建记录参数的list
        List<String> paramList = new ArrayList<String>();
        //得到此路径的文件夹
        File file = new File(path);
        //得到此路径下所有的文件
        File[] files = file.listFiles();
        for (File f : files) {
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader brd = new BufferedReader(isr);

            String b = null;
            while ((b = brd.readLine()) != null) {
                paramList.add(b);
            }
        }
        //查询出起始节点传过来需要查看状态的包在目标节点的状态并返回ack
        List<PackageGlobalStateBean> packageGlobalStateBeans = packageGlobalStateBeanMapper.selectByPrimaryKeys(paramList);
        //创建生成ack的参数map
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        for (PackageGlobalStateBean pgsb : packageGlobalStateBeans) {
            //查询出最后一次的nodeprocess
            DataNodeProcessBean dataNodeProcessBean = dataNodeProcessBeanMapper.selectByUnique(pgsb.getPackageId()+CommonConstants.NAME.ZIP, FileConfigUtil.CURNODEID, FileConfigUtil.CURNODEID);
            if (GlobalState.FINISHED.getCode().equals(pgsb.getGlobalStateDm())) {
                map.put("PACKAGE_ID", pgsb.getPackageId());
                map.put("NODE_ID", nodeId);
                map.put("SEND_STATE_DM", pgsb.getGlobalStateDm());
                map.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JY);
                map.put("TO_NODE_ID", toNodeId);
                map.put("CREATE_TIME", pgsb.getCreateTime());
                map.put("UPDATE_TIME", new Date());
                if (dataNodeProcessBean != null) {
                    map.put("PROCESS_ID", dataNodeProcessBean.getProcessId());
                }
                //循环里边构造出一次参数就发送一个ack
                sendAckService.createUpAck(map);
            }

        }
    }
}
