package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.vo.*;
import com.iflytek.dep.common.utils.ResponseBean;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description: 数据包管理服务
 * @date 2019/2/28--20:29
 */
public interface DataPackageService {

    public PageVo<DataPackageVo> listDataPackage(DataPackageDto dataPackageDto);

    public DataPackageVo getDataPackage(String packageId, String toNodeId);

    public List<PackageVo> listPackageSub(String packageId);

    public List<List<NodeLinkVo>> getNodeLink(PackageVo packageVo);

    public List<NodeLinkDetailVo> getNodeLinkDetail(PackageVo packageVo);

    ResponseBean reTryPackage(String packageId, String toNodeId);

    public void confirmState(String packageId, String toNodeId);

}
