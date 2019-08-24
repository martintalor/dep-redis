package com.iflytek.dep.server.down;

import com.iflytek.dep.server.constants.ActionType;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.PkgType;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.section.SectionNodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建SectionNode 启动线程任务
 *
 * @author Kevin
 */
@Service
public class PkgGetterManger {
    @Autowired
    PkgGetter pkgGetter;

    private static Logger logger = LoggerFactory.getLogger(PkgGetterManger.class);

    /**
     * @描述 mainIntoDatabase中心入库
     * @参数 [exchangeNodeType, packageId, packageFileList, targetFtp, param]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/4/16
     * @修改人和其它信息
     */
    public void mainIntoDatabase(ExchangeNodeType exchangeNodeType, String packageId, List<String> packageFileList, FtpClientTemplate targetFtp, ConcurrentHashMap<String, Object> param) throws Exception {
        SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.MAINDATA, exchangeNodeType, ActionType.DOWN, packageId);
        logger.info("---------------链路的选择：{}",sectionNodeBuilder.build().getClass());
        final String thisPackageId = new String(packageId);
        pkgGetter.down(thisPackageId, sectionNodeBuilder.build(), param);
    }

    /**
     * 下载数据包接口
     *
     * @param packageId
     * @param packageFileList
     * @param targetFtp       目标FTP信息，从哪个FTP上下载数据
     */
    public void downLoadPackage(ExchangeNodeType exchangeNodeType, String packageId, List<String> packageFileList, FtpClientTemplate targetFtp, ConcurrentHashMap<String, Object> param) throws Exception {
        SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.DATA, exchangeNodeType, ActionType.DOWN, packageId);

        final String thisPackageId = new String(packageId);
        pkgGetter.down(thisPackageId, sectionNodeBuilder.build(), param);
    }

    /**
     * 下载Ack接口
     *
     * @param packageId
     * @param packageFileList
     * @param targetFtp       目标FTP信息，从哪个FTP上下载数据
     */
    public void downLoadAck(ExchangeNodeType exchangeNodeType, String packageId, List<String> packageFileList, FtpClientTemplate targetFtp, ConcurrentHashMap<String, Object> param) throws Exception {
        SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.ACK, exchangeNodeType, ActionType.DOWN, packageId);

        final String thisPackageId = new String(packageId);
        pkgGetter.down(thisPackageId, sectionNodeBuilder.build(), param);
    }

}
