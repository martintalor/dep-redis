package com.iflytek.dep.server.dataPack;

import com.iflytek.dep.server.DepApplicationTests;
import com.iflytek.dep.server.monitor.SpringLoadedListener;
import com.iflytek.dep.server.section.FileUnPackSection;
import com.iflytek.dep.server.section.FileUploadSection;
import com.iflytek.dep.server.service.dataPack.SendPackService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.DataPackTests
 * @Description:
 * @date 2019/2/23--20:26
 */

public class SendPackTests extends DepApplicationTests {

    @Autowired
    SendPackService sendPackService;

    @Autowired
    SpringLoadedListener loadedListener;

    @Autowired
    FileUploadSection fileUploadSection;

    @Autowired
    FileUnPackSection fileUnPackSection;

    @Override
    public void contextLoads() throws Exception {
        long startTime = startTest();

        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
//        String packageId = "PKG_APP-G1_TO_APP-F1,APP-J1_1551507480633_da5152af-0ab1-4bc7-b8cf-528d94f93b83.z01";
//        String curNodeId = "G01";
//        paramMap.put("PACKAGE_ID",packageId);
//        paramMap.put("NODE_ID",curNodeId);
//
//        sendPackService.downPack(paramMap);
//        System.out.println("downPack end!");

        String testDir = "D:\\A_PACK\\S\\2019\\03\\22\\PKG_APP-S1_TO_APP-J1,APP-F1_1553255253540_2f05ef42-90d8-4007-b126-9ce0f2a0240b";
        String testName = "\\56f217327dd72e4056f2ec4b9356db69_t.zip";

        String outStr = testDir + "\\un";
        String srcFolder = testDir + "\\dys";
        int unit = 20;
        String fileNames = "kkkk";
//        Zip4JUtil.zipTOSplit(outStr,srcFolder,unit,fileNames);
//        Zip4JUtil.Unzip4j(outStr + "\\kkkk.zip");

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
        ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
        fileNames = "kkkk.z09";
        zip.put("FILE_PATH", srcFolder + "\\" + fileNames);
        zip.put("PACKAGE_ID", fileNames);
        param.add(zip);
        map.put("PARAM",param);
        //    fileUnPackSection.doAct(map);
//        map.put("PACKAGE_ID")
//        fileUnPackSection.doAct();
//        upPackTest();

//        fileUploadSection.doAct(paramMap);
        System.out.println("upPack end!");

        endTest("SendPackTests", startTime);
    }


    private void upPackTest () {
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();

        paramMap = new ConcurrentHashMap<>();
        String packageId = "PKG_APP-G1_TO_APP-F1,APP-J1_1551507480633_da5152af-0ab1-4bc7-b8cf-528d94f93b83.z01";
        String curNodeId = "ZSE01";
        String toNodeId = "F01";
        String filePath = "D:\\aaa\\PKG_APP-G1_TO_APP-F1,APP-J1_1551507480633_da5152af-0ab1-4bc7-b8cf-528d94f93b83\\APP-F1\\" +
                packageId;

        paramMap.put("PACKAGE_ID",packageId);
        paramMap.put("NODE_ID",curNodeId);
        paramMap.put("TO_NODE_ID",toNodeId);
        paramMap.put("FILE_PATH",filePath);
        CopyOnWriteArrayList listMap = new CopyOnWriteArrayList();
        listMap.add(paramMap);
    }

}
