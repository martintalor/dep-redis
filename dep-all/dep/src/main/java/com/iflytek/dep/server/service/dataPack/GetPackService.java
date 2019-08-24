package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.server.mapper.PackageGlobalStateBeanMapper;
import com.iflytek.dep.server.model.UnfinishedPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.server.service.dataPack
 * @Description: GetPackService 获取数据包服务
 * @date 2019/3/22--14:56
 */
@Service
public class GetPackService {

    @Autowired
    private PackageGlobalStateBeanMapper packageGlobalStateBeanMapper;


    public List<UnfinishedPack> getUnfinishedPackageId(String serverNodeId) {
        return packageGlobalStateBeanMapper.getUnfinishedPackageId(serverNodeId);
    }

    public void updateUnfinishedById(String packageId) {
        packageGlobalStateBeanMapper.updateUnfinishedById(packageId);
    }

    public List<UnfinishedPack> getUnfinishedPackageList(String currentNodeId){
        return packageGlobalStateBeanMapper.getUnfinishedPackageList(currentNodeId);
    }
}