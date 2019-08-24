package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.DataPackBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.service.dataPack.sendPack
 * @Description:
 * @date 2019/2/23--14:40
 */
@Repository
public interface SendPackMapper {

    /**
     * 描述：获取数据包列表信息
     * 时间：2019-02-23
     * @author：姚伟-weiyao2
     * @param
     * @return
     */
    List<DataPackBean> getDataPackList(Map<String, Object> map);

    /**
     * 描述：获取单个数据包信息
     * 时间：2019-02-23
     * @author：姚伟-weiyao2
     * @param packageId 数据包id
     * @return DataPackBean 整条数据包信息
     */
    DataPackBean getDataPackInfo(@Param("packageId") String packageId);
}
