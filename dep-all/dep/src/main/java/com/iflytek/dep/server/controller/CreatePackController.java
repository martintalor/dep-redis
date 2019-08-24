package com.iflytek.dep.server.controller;


import com.iflytek.dep.common.pack.Zip4JUtil;
import com.iflytek.dep.common.security.DecryptException;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.down.PkgGetterManger;
import com.iflytek.dep.server.file.FileServiceImpl;
import com.iflytek.dep.server.mapper.DataPackBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.NodeLinkBeanMapper;
import com.iflytek.dep.server.model.DataPackBean;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.section.FileEncryptSection;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.GetPackService;
import com.iflytek.dep.server.up.PkgUploaderManager;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.service.dataPack.createPack
 * @Description:
 * @date 2019/2/25--18:01
 */
@RestController
@RequestMapping("/service/dataPack")
@Api(value = "核心业务流程接口", tags = {"测试"})
public class CreatePackController {
    private static Logger logger = Logger.getLogger(CreatePackController.class);
    @Autowired
    CreatePackService createPackService;
    @Autowired
    DataPackBeanMapper dataPackBeanMapper;

    @Autowired
    FileEncryptSection fileEncryptSection;
    @Autowired
    GetPackService getPackService;


    @Autowired
    PkgGetterManger pkgGetterManger;

    @Autowired
    PkgUploaderManager pkgUploaderManager;

    @Autowired
    FileServiceImpl fileService;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    NodeLinkBeanMapper nodeLinkBeanMapper;

    @Autowired
    Environment environment;

    @Autowired
    RedisUtil redisUtil;

    /**
     * @描述 getFileDir
     * @参数 [appIdFrom, appIdTo]
     * @返回值 java.util.Map<java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Object>
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    @ApiOperation(value = "生成发送文件夹请求接口", notes = "生成发送文件夹请求接口")
    @GetMapping("/getFileDir")
    public Map<String, Object> getFileDir(@RequestParam String appIdFrom, @RequestParam String appIdTo, @RequestParam(name = "sendLevel", required = false, defaultValue = "3") String sendLevel) throws Exception {

        Map<String, Object> fileDir = createPackService.getFileDir(appIdFrom, appIdTo, sendLevel);
        return fileDir;
    }


    @ApiOperation(value = "压缩加密文件请求接口", notes = "压缩加密文件请求接口")
    @PostMapping("/zipFile")
    public String toZip(@RequestParam String packDirPath, @RequestParam String fileName, @RequestParam String mark) throws ZipException, EncryptException {

        //文件压缩并且返回全路径主压缩包
        String zipPath = createPackService.toZip(packDirPath, fileName);
        //得到压缩包所在的文件夹目录
        File zip = new File(zipPath);
        String parentDir = zip.getParent();
        //加密此文件夹下所有压缩包
        boolean encrypt = encryptZips(parentDir, mark, zipPath);
        if (encrypt) {

            return parentDir + CommonConstants.NAME.FILESPLIT + CommonConstants.NAME.ENCRYPT_PREFIX + fileName + CommonConstants.NAME.ZIP;
        }
        return zipPath;
    }

    @ApiOperation(value = "压缩文件ck加密预留接口", notes = "压缩文件ck加密预留接口")
    @PostMapping("/encryptZips")
    public boolean encryptZips(@RequestParam String pathStr, @RequestParam String mark, @RequestParam String zipPath) throws EncryptException {
        if (mark == null) {
            mark = CommonConstants.STATE.SELF;
        }
        //判断用原本系统加密方式，还是用ck预留扩展的加密方式
        if (CommonConstants.STATE.SELF.equals(mark)) {
            String publickey = FileConfigUtil.PUBLICKEY;
            //加密
            createPackService.encryptZips(pathStr, publickey, "z01");
            //删除原没加密压缩包
            PackUtil.deleteZip(zipPath);
            return true;
        }
        if (CommonConstants.STATE.CA.equals(mark)) {

            return true;
        }
        return false;
    }


    @ApiOperation(value = "公钥发放接口", notes = "公钥发放接口")
    @GetMapping("/createKeys")
    public boolean createKeys(@RequestParam String serverNode) throws IOException, NoSuchAlgorithmException {
        boolean keys = createPackService.createKeys(serverNode);
        return keys;
    }

    @ApiOperation(value = "解密", notes = "包解密")
    @GetMapping("/decryptZips")
    public boolean decryptZips(@RequestParam String pathStr, @RequestParam String mark) throws DecryptException {
        if (mark == null) {
            mark = CommonConstants.STATE.SELF;
        }
        //判断用原本系统加密方式，还是用ck预留扩展的加密方式
        if (CommonConstants.STATE.SELF.equals(mark)) {
            createPackService.decryptZips(pathStr);
            return true;
        }
        if (CommonConstants.STATE.CA.equals(mark)) {

            return true;
        }
        return false;
    }


    @ApiOperation(value = "业务调用1", notes = "业务调用1")
    @PostMapping("/startTransform")
    public void startTransform(@RequestParam String pathStr, @RequestParam String fileName) throws Exception {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("PACK_DIR_PATH", pathStr);
        map.put("FILE_NAME", fileName);

        redisUtil.pushUpTask(pathStr, fileName);
//        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, fileName, null, null, map);


    }

    @ApiOperation(value = "业务调用2", notes = "业务调用2")
    @PostMapping("/doJob")
    public void doZipJob(@RequestParam String pathStr, @RequestParam String fileName) throws Exception {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("PACK_DIR_PATH", pathStr);
        map.put("FILE_NAME", fileName);

        redisUtil.pushUpTask(pathStr, fileName);
//        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, fileName, null, null, map);


    }

    @ApiOperation(value = "业务调用3", notes = "业务调用3")
    @PostMapping("/doMainUpJob")
    public void doMainUpJob(@RequestParam String pathStr, @RequestParam String fileName) throws Exception {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("PACK_DIR_PATH", pathStr);
        map.put("FILE_NAME", fileName);

        redisUtil.pushUpTask(pathStr, fileName);
//        pkgUploaderManager.mainUploadPackage(ExchangeNodeType.MAIN, fileName, null, null, map);


    }

    @ApiOperation(value = "压力测试", notes = "压力测试，callNum为调用次数")
    @PostMapping("/pressureTest")
    public void pressureTest(@RequestParam Integer callNum, @RequestParam String appIdFrom, @RequestParam String appIdTo, @RequestParam Integer size) throws Exception {

        for (int i = 0; i < callNum; i++) {

            new Thread(() -> {
                try {
                    doZipJobDev(appIdFrom, appIdTo, size);
                } catch (Exception e) {
                    logger.error(e);
                    Thread.currentThread().interrupt();
                }
            }).start();

        }

    }

    @ApiOperation(value = "开发测试用接口", notes = "开发测试")
    @PostMapping("/doJobDev")
    public Map doZipJobDev(@RequestParam String appIdFrom, @RequestParam String appIdTo, @RequestParam Integer size) throws Exception {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        if (appIdFrom == null || appIdTo == null) {
            logger.info("请传入发送应用id和接收应用id");
        }

        // 随机队列
        Map<String, Object> fileDir = getFileDir(appIdFrom, appIdTo, String.valueOf((int) (Math.random() * 9)));
        String pathStr = String.valueOf(fileDir.get("path"));
        String fileName = String.valueOf(fileDir.get("fileName"));

        File file = new File(pathStr);
        if (!file.exists()) {
            file.mkdirs();
        }
        long start = System.currentTimeMillis();
//        File template = ResourceUtils.getFile("classpath:template/template.docx");

        String tempPath = environment.getProperty("template.path");
        File template = new File(tempPath);
//        File template = new File("/workspace/template/template.docx");
//        File template = new File("/workspace/iflytek/template/template.docx");
        String newFileName = "";
        for (int i = 0; i < size; i++) {
            String fileName1 = "auto-test1" + i;

//            File f = new File(pathStr, fileName1);
//            if (!f.exists()) {
//                CreateFile.createFile(f, 1024l * 1024l);
//            }
            if (template.exists()) {
                newFileName = pathStr + "/" + i + "-" + template.getName();
                System.out.println(newFileName);
                FileUtils.copyFile(template, new File(newFileName));
            } else {
//                File f = new File(pathStr, fileName1);
//                if (!f.exists()) {
//                    CreateFile.createFile(f, 1024l * 1024l);
//                }
                throw new IOException("template is not exists:" + template);
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("total times " + (end - start));
        map.put("PACK_DIR_PATH", pathStr);
        map.put("FILE_NAME", fileName);

//        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, fileName, null, null, map);

        // 从直接调用打包方法变成先写入redis
        redisUtil.pushUpTask(pathStr, fileName);

        return fileDir;
    }

    @ApiOperation(value = "重试接口", notes = "重试接口")
    @GetMapping("/retryDev")
    public void retryDev(@RequestParam String packageId) throws Exception {
//        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

        redisUtil.pushUpTask(packageId, packageId);
//        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, packageId, null, null, map);
    }


    @ApiOperation(value = "叶子节点重试上传接口", notes = "叶子节点重试上传")
    @PostMapping("/leafRetryUp")
    public void leafRetryUp(@RequestParam String fileName) throws Exception {
        logger.info("叶子节点上传------压缩包：" + fileName + "开始重试");
        // 更新状态
        getPackService.updateUnfinishedById(fileName);
        //获取包的相关参数
        String pageId = fileName.split("\\.")[0] + CommonConstants.NAME.ZIP;
        DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(pageId);
        String folderPath = null;
        String packagePath = null;
        if (dataPackBean != null) {
            folderPath = dataPackBean.getFolderPath();
            packagePath = dataPackBean.getPackagePath();
        }
        //清空以此包名命名的文件夹
       /* if (StringUtil.isNotEmpty(packagePath) ) {
            FileUtil.delAllFile(packagePath);
        }*/
        //然后调用上传链路
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("PACK_DIR_PATH", folderPath);
        map.put("FILE_NAME", fileName.split("\\.")[0]);
        map.put("PACKAGE_PATH", packagePath);

        redisUtil.pushUpTask(folderPath, fileName);
//        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, fileName, null, null, map);
        logger.info("叶子节点上传-------压缩包：" + fileName + "重试成功");

    }

    @ApiOperation(value = "中心节点下载重试接口", notes = "中心节点下载重试")
    @PostMapping("/mainRetryDown")
    public void mainRetryDown(@RequestParam String fileName) throws Exception {
        logger.info("中心节点下载-------压缩包：" + fileName + "开始重试");
        // 更新状态
        getPackService.updateUnfinishedById(fileName.split("\\.")[0]);
       /* //先获取到下载目录
        String localPath = fileService.makeDirByPackageId(fileName) + File.separator;
        //清空下载目录
        //FileUtil.delAllFile(localPath);
        //查找当前ftp节点
        // 第一步根据appIdTo 获取对应node节点
        //多目的地的话截取第一个来找到对应链路，找到目标ftp下载
        NodeAppBean toApp = nodeAppBeanMapper.selectByPrimaryKey(PackUtil.splitAppTo(fileName)[0]);
        String toNodeId = toApp.getNodeId();// 目标节点id
        // 第二步输入参数，packageId、appIdTo、curNodeId，查找当前节点链路是否已经存在，已存在，则返回当前环节链接
        NodeLinkBean nodeLinkBean = new NodeLinkBean();
        nodeLinkBean.setPackageId(fileName);
        nodeLinkBean.setToNodeId(toNodeId);
        nodeLinkBean.setRightNodeId(FileConfigUtil.CURNODEID);
        nodeLinkBean = nodeLinkBeanMapper.getLinkByRightNode(nodeLinkBean);
        String curNodeId = null;
        if (nodeLinkBean != null) {
            curNodeId=nodeLinkBean.getLeftNodeId();
        }*/
        //获取包的相关参数
        String pageId = fileName.split("\\.")[0] + CommonConstants.NAME.ZIP;
        DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(pageId);
        String folderPath = null;
        String packagePath = null;
        if (dataPackBean != null) {
            folderPath = dataPackBean.getFolderPath();
            packagePath = dataPackBean.getPackagePath();
        }
        //查询此包是否是从中心发出
        String appFrom = PackUtil.splitAppFrom(fileName);
        NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appFrom);

        if (nodeAppBean != null & nodeAppBean.getNodeId().equals(FileConfigUtil.CURNODEID)) {
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
            map.put("PACK_DIR_PATH", folderPath);
            map.put("FILE_NAME", fileName.split("\\.")[0]);
            map.put("PACKAGE_PATH", packagePath);

            pkgUploaderManager.mainUploadPackage(ExchangeNodeType.MAIN, fileName, null, null, map);
            logger.info("中心节点上传-------压缩包：" + fileName + "重试成功");

        } else {
            // 传递参数
            ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
            paramMap.put("PACKAGE_ID", fileName);// 数据包名
            //paramMap.put("NODE_ID", curNodeId);// 当前FTP节点
            //调用下载链路
            pkgGetterManger.downLoadPackage(ExchangeNodeType.MAIN, fileName, null, null, paramMap);
            logger.info("中心节点下载-------压缩包：" + fileName + "重试成功");
        }

    }

    @ApiOperation(value = "叶子节点下载重试接口", notes = "叶子节点下载重试")
    @PostMapping("/leafRetryDown")
    public void leafRetryDown(@RequestParam String fileName) throws Exception {
        logger.info("叶子节点下载-------压缩包：" + fileName + "开始重试");
        // 更新状态
        getPackService.updateUnfinishedById(fileName.split("\\.")[0]);
        //先获取到下载目录
        //String localPath = fileService.makeDirByPackageId(fileName) + File.separator;
        //清空下载目录
        //FileUtil.delAllFile(localPath);
        //查找当前ftp节点
        // 第一步根据appIdTo 获取对应node节点
       /* String toNodeId = FileConfigUtil.CURNODEID;// 目标节点id
        // 第二步输入参数，packageId、appIdTo、curNodeId，查找当前节点链路是否已经存在，已存在，则返回当前环节链接
        NodeLinkBean nodeLinkBean = new NodeLinkBean();
        nodeLinkBean.setPackageId(fileName);
        nodeLinkBean.setToNodeId(toNodeId);
        nodeLinkBean.setRightNodeId(toNodeId);
        nodeLinkBean = nodeLinkBeanMapper.getLinkByRightNode(nodeLinkBean);
        String curNodeId = null;
        if (nodeLinkBean != null) {
            curNodeId=nodeLinkBean.getLeftNodeId();
        }*/
        // 传递参数
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
        paramMap.put("PACKAGE_ID", fileName);// 数据包名
        //paramMap.put("NODE_ID", curNodeId);// 当前FTP节点
        //调用下载链路
        pkgGetterManger.downLoadPackage(ExchangeNodeType.LEAF, fileName, null, null, paramMap);
        logger.info("叶子节点下载-------压缩包：" + fileName + "重试成功");
    }

    @ApiOperation(value = "解压缩测试用", notes = "解压缩接口")
    @GetMapping("/unzip")
    public void unzip(@RequestParam String filePath){


        try {
            Zip4JUtil.Unzip4j(filePath.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
        } catch (ZipException e) {
            logger.error("",e);
        }
    }

}
