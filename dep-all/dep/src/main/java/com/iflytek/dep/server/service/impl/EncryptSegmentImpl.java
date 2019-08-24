package com.iflytek.dep.server.service.impl;

import com.iflytek.dep.server.service.SegmentService;
import com.iflytek.dep.server.mapper.DataPackBeanMapper;
import com.iflytek.dep.server.mapper.PackageCurrentStateBeanMapper;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.utils.CommonConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密模块
 */
@Service
@Scope("prototype")
public class EncryptSegmentImpl implements SegmentService {
    private CreatePackService createPackService;
    private DataPackBeanMapper dataPackBeanMapper;
    private PackageCurrentStateBeanMapper packageCurrentStateBeanMapper;
    private FtpUploadFileSegmentImpl ftpUploadFileSegment;

    public FtpUploadFileSegmentImpl getFtpUploadFileSegment() {
        return ftpUploadFileSegment;
    }

    public void setFtpUploadFileSegment(FtpUploadFileSegmentImpl ftpUploadFileSegment) {
        this.ftpUploadFileSegment = ftpUploadFileSegment;
    }

    public CreatePackService getCreatePackService() {
        return createPackService;
    }

    public void setCreatePackService(CreatePackService createPackService) {
        this.createPackService = createPackService;
    }

    public DataPackBeanMapper getDataPackBeanMapper() {
        return dataPackBeanMapper;
    }

    public void setDataPackBeanMapper(DataPackBeanMapper dataPackBeanMapper) {
        this.dataPackBeanMapper = dataPackBeanMapper;
    }

    public PackageCurrentStateBeanMapper getPackageCurrentStateBeanMapper() {
        return packageCurrentStateBeanMapper;
    }

    public void setPackageCurrentStateBeanMapper(PackageCurrentStateBeanMapper packageCurrentStateBeanMapper) {
        this.packageCurrentStateBeanMapper = packageCurrentStateBeanMapper;
    }

    @Override
    public void doJob(ConcurrentHashMap<String, Object> map) throws Exception {
        //得到压缩主包全文件路径
        String zipPath = String.valueOf(map.get("zip"));
        //得到加密方式
        String mark = null;
        Object state = map.get("mark");
        if (state == null) {
            mark = CommonConstants.STATE.SELF;
        }else{
            mark=String.valueOf(state);
        }
        //得到压缩包所在的文件夹目录
        File zip = new File(zipPath);
        String parentDir = zip.getParent();
        //加密相关压缩包
        //判断用原本系统加密方式，还是用ck预留扩展的加密方式
        if (CommonConstants.STATE.SELF.equals(mark)) {
            //createPackService.encryptZips(parentDir);

        }
        if (CommonConstants.STATE.CA.equals(mark)) {

        }

        ConcurrentHashMap<String,Object> result =new ConcurrentHashMap<String, Object>();
        result.put("","");
        this.next(result);
    }

    @Override
    public void next(ConcurrentHashMap<String, Object> map) throws Exception {
        if (this.getFtpUploadFileSegment() != null) {
            this.getFtpUploadFileSegment().doJob(map);
        }
    }
}
