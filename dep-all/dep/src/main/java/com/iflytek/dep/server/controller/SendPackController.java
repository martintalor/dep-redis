package com.iflytek.dep.server.controller;


import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.section.FileUploadSection;
import com.iflytek.dep.server.service.dataPack.ParseAckService;
import com.iflytek.dep.server.service.dataPack.SendAckService;
import com.iflytek.dep.server.service.dataPack.SendPackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姚伟
 * @version V1.0
 * @Package com.iflytek.dep.service.dataPack.createPack
 * @Description:
 * @date 2019/3/11
 */
@RestController
@RequestMapping("/service/sendPack")
@Api(value="上传",tags={"上传接口"})
public class SendPackController {
    private static Logger logger = Logger.getLogger(SendPackController.class);
    @Autowired
    SendPackService sendPackService;

    @Autowired
    SendAckService sendAckService;


    @Autowired
    ParseAckService parseAckService;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    FileUploadSection fileUploadSection;

    @Autowired
    UpStatusService upStatusService;


    @ApiOperation(value = "上传多目标", notes = "多目标上传")
    @PostMapping("/upPackListTestMain")
    public void upPackListTestMain(@RequestParam String pathFolder )  {
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();

        String curNodeId = FileConfigUtil.CURNODEID;
        String filePath = pathFolder;

        ArrayList list = new ArrayList();

        File file = new File(filePath);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {

                System.out.println("文     件："+tempList[i]);

                String packageId = tempList[i].getName();
                String[] appIdTos = PackUtil.splitAppTo(packageId);

                for (String appIdTo : appIdTos ) {

                    NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdTo);

                    paramMap = new ConcurrentHashMap<>();
                    paramMap.put( "PACKAGE_ID", packageId );
                    paramMap.put( "NODE_ID", curNodeId );
                    paramMap.put( "TO_NODE_ID", nodeAppBean.getNodeId() );
                    paramMap.put( "FILE_PATH", tempList[i].getAbsolutePath() );
                    list.add(paramMap);
                }

            }

            if (tempList[i].isDirectory()) {
                System.out.println("文件夹："+tempList[i]);
            }

        }// for end

        paramMap = new ConcurrentHashMap<>();
        paramMap.put("PARAM", list);

        System.out.println("list >>>>" + list.toString());

        try {
//            sendPackService.upPackList(paramMap);
//            fileUploadSection.doAct(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试单节点上传
    @ApiOperation(value = "上传单目标", notes = "单目标上传")
    @PostMapping("/upPackListTestLeaf")
    public void upPackListTest(@RequestParam String pathFolder, @RequestParam String toNode) {
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();

        String curNodeId = FileConfigUtil.CURNODEID;
        String filePath = pathFolder;

        ArrayList list = new ArrayList();

        File file = new File(filePath);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {

                System.out.println("文     件："+tempList[i]);

                String packageId = tempList[i].getName();

                paramMap = new ConcurrentHashMap<>();
                paramMap.put( "PACKAGE_ID", packageId );
                paramMap.put( "NODE_ID", curNodeId );
                paramMap.put( "TO_NODE_ID", toNode );
                paramMap.put( "FILE_PATH", tempList[i].getAbsolutePath() );
                list.add(paramMap);

            }

            if (tempList[i].isDirectory()) {
                System.out.println("文件夹："+tempList[i]);
            }

        }// for end

        paramMap = new ConcurrentHashMap<>();
        paramMap.put("PARAM", list);

        System.out.println("list >>>>" + list.toString());

        try {
//            sendPackService.upPackList(paramMap);
//            fileUploadSection.doAct(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 测试ack包解析
    @ApiOperation(value = "解析ACK", notes = "ACK解析")
    @PostMapping("/parseAck")
    public void parseAckTest(@RequestParam String packageId) throws Exception{
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
//        String packageId = "ACK#ZSE01#J01#20#99#1552880261845#PKG_APP-S1_TO_APP-J1_1552880224485_29d04504-1006-4eed-8276-775f30ee1c50cc.zip";
        paramMap.put("PACKAGE_ID",packageId);
        parseAckService.parseAck(paramMap);
    }

    // 测试ack包生成
    @ApiOperation(value = "生成ACK", notes = "生成ack")
    @PostMapping("/createUpAck")
    public String createAckTest(@RequestParam String packageId,
                              @RequestParam String curNodeId,
                              @RequestParam String toNodeId,
                              @RequestParam String sendStateDm,
                              @RequestParam String operateStateDm) {
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
//        String packageId = "ACK#ZSE01#J01#20#99#1552880261845#PKG_APP-S1_TO_APP-J1_1552880224485_29d04504-1006-4eed-8276-775f30ee1c50cc.zip";


        paramMap.put("PACKAGE_ID",packageId);
        paramMap.put("NODE_ID", curNodeId);
        paramMap.put("TO_NODE_ID", toNodeId);
        paramMap.put("SEND_STATE_DM", sendStateDm);
        paramMap.put("OPERATE_STATE_DM", operateStateDm);
        paramMap.put("CREATE_TIME",new Date());
        paramMap.put("UPDATE_TIME",new Date());
        paramMap.put("PROCESS_ID","");

        String ackFix = CommonConstants.NAME.ACK_FIX;
        String ackName = "ACK" + ackFix + curNodeId
                + ackFix + toNodeId
                + ackFix + sendStateDm
                + ackFix + operateStateDm
                + ackFix + packageId
                + ackFix + new Date().getTime()
                + ackFix + new Date().getTime()
                + ackFix + DateUtils.getTodaySN();// 年月日

//        sendAckService.createUpAck(paramMap);
        return ackName;
    }

    @ApiOperation(value = "测试链路生成", notes = "链路生成")
    @PostMapping("/testLink")
    public void testLink(@RequestParam String packageID, @RequestParam String curNodeId) throws Exception {

        upStatusService.createNodeLinks(packageID, curNodeId);

    }


}
