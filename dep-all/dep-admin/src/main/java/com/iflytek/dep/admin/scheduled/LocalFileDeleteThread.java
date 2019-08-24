package com.iflytek.dep.admin.scheduled;

import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.SpringUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * DEP-SERVER本地文件删除定时任务线程
 *
 * @author xiliu5
 *
 */
public class LocalFileDeleteThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(LocalFileDeleteThread.class);

    private static Integer fileMinutes = null;

    private static String packedDir = null;

    private static String packingDir = null;

    public LocalFileDeleteThread() {
    }

    @Override
    public void run() {
        try {
            File fileRoot = new File(getPackedDir());

            deleteFiles(fileRoot);

            File packing = new File(getPackingDir());

            deleteFiles(packing);

            logger.info("\nLocalFileDeleteThread 执行成功");
        } catch (Exception e) {
            logger.error("\nLocalFileDeleteThread 执行失败: " + e.getLocalizedMessage());
        } finally {
        }
    }

    private void deleteFiles(File fileRoot) {
        if (fileRoot.isDirectory()) {
            if (fileRoot.getName().startsWith("PKG_")) {
                if ((new Date().getTime() - fileRoot.lastModified())/(60*1000)> getMinutesInt()) {//最后修改时间距现在时间大于3天
                    try {
                        FileUtils.forceDelete(fileRoot);
                    } catch (Exception e) {
                        logger.error("删除文件失败, file=" + fileRoot.getName());
                    } finally {
                    }
                }
            } else {//不是数据包文件夹，递归向下查找数据包文件夹
                for (File file : fileRoot.listFiles()) {
                    deleteFiles(file);
                }
            }
        }

    }

    /**
     * 从配置文件中读取本地文件删除的时间间隔，默认7天
     * @return
     */
    private int getMinutesInt() {
        int minutesInt = 7*24*60;//默认7天

        if (this.fileMinutes == null) {
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            String minutes = propertiesUtils.getPropertiesValue("fileDelete.minutes.local");

            try {
                minutesInt = Integer.valueOf(minutes);
            } catch (NumberFormatException e) {
                logger.error("本地文件删除时间间隔天数解析失败，默认为7天");
            }

            this.fileMinutes = minutesInt;
        }


        return this.fileMinutes;
    }

    private String getPackedDir() {
        if (this.packedDir == null) {
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            String packedDirTmp = propertiesUtils.getPropertiesValue("packed.dir");

            this.packedDir = packedDirTmp;
        }

        return this.packedDir;
    }

    private String getPackingDir() {
        if (this.packingDir == null) {
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            String packedDirTmp = propertiesUtils.getPropertiesValue("pack.dir");

            this.packingDir = packedDirTmp;
        }

        return this.packingDir;
    }
}