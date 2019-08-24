package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.common.pack.FileUtil;
import com.iflytek.dep.common.security.DecryptException;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.server.file.FileServiceImpl;
import com.iflytek.dep.server.file.ZipServiceImpl;
import com.iflytek.dep.server.mapper.DataPackBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.InJobDto;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.ResponseBean;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.service.dataPack.createPack
 * @Description:
 * @date 2019/2/23--10:57
 */
@Service
public class CreatePackService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CreatePackService.class);
    @Autowired
    DataPackBeanMapper dataPackBeanMapper;
    @Autowired
    FileServiceImpl fileService;
    @Autowired
    ZipServiceImpl zipService;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private Environment environment;



    /**
     *@描述  notifyEtl
     *@参数  [packageId]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/4/23
     *@修改人和其它信息
     */
    //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 10000l, multiplier = 2))
    public void notifyEtl(String packageId) throws Exception {
        logger.info("------------------开始调用etl入库接口：{}", packageId);
        String[] toApps = PackUtil.splitAppTo(packageId);
        NodeAppBean nodeAppBean = null;
        for (String itemApp : toApps) {
            nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(itemApp);
            if (FileConfigUtil.CURNODEID.equals(nodeAppBean.getNodeId())) {
                break;
            }
        }
        logger.info("----------------判断成功是某个nodeapp{}", nodeAppBean.getClass().toString());

        if (nodeAppBean != null) {
            if (nodeAppBean.getCalUrl() != null) {
                logger.info("----------------------接口地址：{}", nodeAppBean.getCalUrl());
                //logger.info("----------------------接口地址加参数：{}", nodeAppBean.getCalUrl() + environment.getProperty("packed.dir") + "/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, "") + "/unpack/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, ""));
                //ResponseBean forObject = restTemplate.getForObject(nodeAppBean.getCalUrl() + environment.getProperty("packed.dir") + "/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, "") + "/unpack/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, ""), ResponseBean.class);
                InJobDto inJobDto = new InJobDto();
                inJobDto.setInPath(environment.getProperty("packed.dir") + "/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, "") + "/unpack/" + packageId.split("\\.")[0].replaceAll(CommonConstants.NAME.APPSPLIT, ""));
                inJobDto.setPackageId(packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0]);
                ResponseBean responseBean = restTemplate.postForObject(nodeAppBean.getCalUrl(), inJobDto, ResponseBean.class);
                logger.info("-------------------调用接口成功");
            }
        }
    }

    /**
     * @描述 getFileDir
     * @参数 [appIdFrom, appIdTo]
     * @返回值 java.util.Map<java.lang.String       ,       java.lang.Object>
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    public Map<String, Object> getFileDir(String appIdFrom, String appIdTo, String level) throws Exception {
        Map result = new HashMap();

        if (appIdFrom == null || appIdTo == null) {
            logger.info("请传入发送应用id和接收应用id");
            result.put("flag",false);
            result.put("message","请传入发送应用id和接收应用id");
            return result;
        }

        if (appIdTo.contains(appIdFrom)) {
            logger.info("fromId不能为toId");
            result.put("flag",false);
            result.put("message","fromId不能为toId");
            return result;
        }

        NodeAppBean nodeAppBeanFrom = nodeAppBeanMapper.selectByPrimaryKey(appIdFrom);
        String[] splits = appIdTo.split(CommonConstants.NAME.APPSPLIT);
        for(String split:splits){
            NodeAppBean nodeAppBeanTo = nodeAppBeanMapper.selectByPrimaryKey(split);
            if (nodeAppBeanFrom == null || nodeAppBeanTo == null) {
                logger.info("传入参数不合法");
                result.put("flag",false);
                result.put("message","传入参数不合法");
                return result;
            }
        }

        String fromTo = CommonConstants.NAME.FROM + appIdFrom + CommonConstants.NAME.TO + appIdTo;
        Map<String, Object> fileDir = fileService.getFileDir(fromTo,level);
        return fileDir;
    }

    /**
     * @描述 toZip
     * @参数 [packDirPath, fileName]
     * @返回值 java.lang.String
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    //@Retryable(value = {ZipException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public String toZip(String packDirPath, String fileName) throws ZipException {
        //压缩文件存放主目录
        String packedDir = FileConfigUtil.PACKEDDIR;
        String outStr = fileService.makeOutFileDir(packedDir, fileName);
        //压缩前先清空存放压缩文件目录
        FileUtil.delAllFile(outStr);
        //压缩
        String zipPath = zipService.toZip(outStr, packDirPath, fileName);
        return zipPath;
    }

    /**
     * @描述 encryptZips
     * @参数 [pathStr, publicKey, appTo]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    //@Retryable(value = {EncryptException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void encryptZips(String pathStr, String publicKey, String appTo) throws EncryptException {
        fileService.encryptZips(pathStr, publicKey, appTo);
    }

    /**
     * @描述 encryptZip
     * @参数 [pathStr, publicKey, appTo]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/6
     * @修改人和其它信息
     */
    //@Retryable(value = {EncryptException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void encryptZip(String pathStr, String publicKey, String appTo) throws EncryptException {
        //先得到此文件
        File file = new File(pathStr);
        fileService.encryptZip(file, publicKey, appTo);
    }

    /**
     * @描述 createKeys
     * @参数 [serverNode]
     * @返回值 boolean
     * @创建人 朱一帆
     * @创建时间 2019/3/2
     * @修改人和其它信息
     */
    //@Retryable(value = {IOException.class, NoSuchAlgorithmException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public boolean createKeys(String serverNode) throws IOException, NoSuchAlgorithmException {
        fileService.createKeys(serverNode);
        return true;
    }

    /**
     * @描述 decryptZips
     * @参数 [pathStr]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/2
     * @修改人和其它信息
     */
    //@Retryable(value = {DecryptException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void decryptZips(String pathStr) throws DecryptException {
        fileService.decryptZips(pathStr);

    }

    /**
     * @描述 decryptZip
     * @参数 [pathStr]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/6
     * @修改人和其它信息
     */
    //@Retryable(value = {DecryptException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void decryptZip(String pathStr) throws DecryptException {
        File file = new File(pathStr);
        fileService.decryptZip(file);

    }

    /**
     * @描述 decryptLeafZip
     * @参数 [pathStr]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/27
     * @修改人和其它信息
     */
    //@Retryable(value = {DecryptException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void decryptLeafZip(String pathStr) throws DecryptException {
        File file = new File(pathStr);
        fileService.decryptLeafZip(file);

    }

    /**
     * @描述 unZip
     * @参数 [pathStr]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/2
     * @修改人和其它信息
     */
    //@Retryable(value = {ZipException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void unZip(String pathStr) throws ZipException {
        zipService.unZip(pathStr);
    }


}
