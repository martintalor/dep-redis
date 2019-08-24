package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.NodeApp;
import com.iflytek.dep.admin.model.dto.NodeAppDto;
import com.iflytek.dep.admin.model.vo.NodeAppVo;
import com.iflytek.dep.admin.model.vo.PageVo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/2/26--21:04
 */
public interface NodeAppNodeService {

    public PageVo<NodeAppVo> listNodeApp(NodeAppDto nodeAppDto);

    public int addNodeApp(NodeApp nodeApp);

    public int updateNodeApp(NodeApp nodeApp);

    public int deleteNodeApp(String appId);
}
