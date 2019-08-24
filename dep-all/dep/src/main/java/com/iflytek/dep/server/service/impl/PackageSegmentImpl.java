/*
package com.iflytek.dep.server.service.impl;

import com.iflytek.dep.server.mapper.DataPackBeanMapper;
import com.iflytek.dep.server.mapper.DataPackSubBeanMapper;
import com.iflytek.dep.server.mapper.PackageCurrentStateBeanMapper;
import com.iflytek.dep.server.model.DataPackBean;
import com.iflytek.dep.server.model.DataPackSubBean;
import com.iflytek.dep.server.model.PackageCurrentStateBean;
import com.iflytek.dep.server.service.SegmentService;
import com.iflytek.dep.server.service.dataPack.createPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.sendPack.SendPackService;
import com.iflytek.dep.server.utils.CommonConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * 打包服务实现类
 *//*


@Service
@Scope("prototype")
public class PackageSegmentImpl implements SegmentService {
    private String jobId;
    private EncryptSegmentImpl encryptSegment;
    private CreatePackService createPackService;
    private DataPackBeanMapper dataPackBeanMapper;
    private PackageCurrentStateBeanMapper packageCurrentStateBeanMapper;
    private SendPackService sendPackService;
    private DataPackSubBeanMapper dataPackSubBeanMapper;

    public DataPackSubBeanMapper getDataPackSubBeanMapper() {
        return dataPackSubBeanMapper;
    }

    public void setDataPackSubBeanMapper(DataPackSubBeanMapper dataPackSubBeanMapper) {
        this.dataPackSubBeanMapper = dataPackSubBeanMapper;
    }

    public SendPackService getSendPackService() {
        return sendPackService;
    }

    public void setSendPackService(SendPackService sendPackService) {
        this.sendPackService = sendPackService;
    }

    public CreatePackService getCreatePackService() {
        return createPackService;
    }

    public void setCreatePackService(CreatePackService createPackService) {
        this.createPackService = createPackService;
    }

    public EncryptSegmentImpl getEncryptSegment() {
        return encryptSegment;
    }

    public void setEncryptSegment(EncryptSegmentImpl encryptSegment) {
        this.encryptSegment = encryptSegment;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    */
/**
     *@描述  doJob 任务调度打包和数据持久化操作
     *@参数  [map]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/2/28
     *@修改人和其它信息
     *//*

    @Override
    public void doJob(ConcurrentHashMap<String, Object> map) throws Exception {
        //获取需要参数
        String packDirPath = String.valueOf(map.get("packDirPath"));
        String fileName = String.valueOf(map.get("fileName"));
        String appIdFrom = String.valueOf(map.get("appIdFrom"));
        String appIdTo = String.valueOf(map.get("appIdTo"));
        String nodeID = null;
        String linkId = null;
        //需要持久化参数准备
        DataPackBean page = new DataPackBean();
        PackageCurrentStateBean pageState = new PackageCurrentStateBean();
        DataPackSubBean pageSub = new DataPackSubBean();
        ConcurrentHashMap<String, String> map1 = new ConcurrentHashMap<String, String>();
        page.setAppIdFrom(appIdFrom);
        page.setAppIdTo(appIdTo);
        page.setFolderPath(packDirPath);
        //压缩
        String path = this.getCreatePackService().toZip(packDirPath, fileName);
        File oldFile = new File(path);
        page.setPackageId(oldFile.getName());
        //通过包名生成链路信息
        ConcurrentHashMap nodeLink = this.getSendPackService().createNodeLink(page);
        nodeID = String.valueOf(nodeLink.get("NODE_ID"));
        linkId = String.valueOf(nodeLink.get("LINK_ID"));
        //通过全路径得到压缩包文件夹
        String pagePath = oldFile.getParent();
        page.setPackagePath(pagePath);
        File newFile = new File(pagePath);
        //遍历此文件夹将文件信息入库
        for (File file : newFile.listFiles()) {
            String pageId = file.getName();
            Integer pageSize = new Long(file.length() / 1024l / 1024l).intValue();
            //判断是否是主包分别插入数据包表和数据包子表
            if(CommonConstants.NAME.ZIP.equals(pageId.substring(pageId.length()-4))){
                page.setPackageId(pageId);
                page.setPackageSize(pageSize);
                this.getDataPackBeanMapper().insert(page);
            }else{
                pageSub.setPackageId(fileName+CommonConstants.NAME.ZIP);
                pageSub.setSubPackageId(pageId);
                pageSub.setPackageSize(pageSize);
            }
            map1.put("LINK_ID", linkId);
            map1.put("NODE_ID", nodeID);
            map1.put("PACKAGE_ID", pageId);
            //插入数据扭转进程表
            String processId = this.getSendPackService().createNodePorcess(map1);
            ConcurrentHashMap<String,String> map2=new ConcurrentHashMap<String, String>();
            map2.put("LINK_ID",linkId);
            map2.put("NODE_ID",nodeID);
            map2.put("OPERATE_STATE_DM",CommonConstants.OPERATESTATE.YS);
            map2.put("PACKAGE_ID",pageId);
            map2.put("PROCESS_ID",processId);
            //插入数据包当前状态表和数据包流水状态表
            this.getSendPackService().updateOperateState(map2);
        }

        ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<String, Object>();
        result.put("zip", path);
        result.put("packDirPath", packDirPath);
        result.put("fileName", fileName);
        result.put("mark", CommonConstants.STATE.SELF);

        this.next(result);
    }

    @Override
    public void next(ConcurrentHashMap<String, Object> map) throws Exception {
        if (this.getEncryptSegment() != null) {
            this.getEncryptSegment().doJob(map);
        }
    }

}
*/
