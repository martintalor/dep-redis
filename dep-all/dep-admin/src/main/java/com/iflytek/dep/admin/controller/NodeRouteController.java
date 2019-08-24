package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.NodeRoute;
import com.iflytek.dep.admin.model.dto.NodeRouteDto;
import com.iflytek.dep.admin.model.vo.NodeRouteVo;
import com.iflytek.dep.admin.service.NodeRouteService;
import com.iflytek.dep.admin.utils.EscapeSql;
import com.iflytek.dep.common.annotation.Auth;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/2/27
 */
@Api(value = "路由管理接口类", tags = {"路由管理接口类"})
@RestController
@RequestMapping("nodeRoute")
@Auth
public class NodeRouteController {

    @Autowired
    private NodeRouteService nodeRouteService;

    Logger logger = LoggerFactory.getLogger(NodeRouteController.class);

    @ApiOperation(value = "查询路由列表", notes = "查询路由列表")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseBean list(@RequestBody NodeRouteDto nodeRouteDto) {
        try {

            if (StringUtils.isEmpty(nodeRouteDto.getRouteName())) {
                nodeRouteDto.setRouteName(null);
            } else {
                nodeRouteDto.setRouteName(EscapeSql.escapeSql(nodeRouteDto.getRouteName()));
            }
            if (StringUtils.isEmpty(nodeRouteDto.getCreateTimeStart())) {
                nodeRouteDto.setCreateTimeStart(null);
            }
            if (StringUtils.isEmpty(nodeRouteDto.getCreateTimeEnd())) {
                nodeRouteDto.setCreateTimeEnd(null);
            }

            return new ResponseBean(nodeRouteService.list(nodeRouteDto));
        } catch (Exception e) {
            logger.error("\n查询路由列表失败:", e);
            return new ResponseBean("查询路由列表失败");
        }
    }

    @ApiOperation(value = "添加路由配置", notes = "添加路由配置")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseBean add(@RequestBody NodeRouteDto nodeRoute) {
        try {
            //判断是否已经存在
            NodeRouteVo node = nodeRouteService.selectByPrimarykey(nodeRoute.getLeftNodeId(), nodeRoute.getRightNodeId());
            if (node != null) {
                return new ResponseBean("该路由已存在！");
            }

            NodeRoute nodeRouteDest = new NodeRoute();
            BeanUtils.copyProperties(nodeRoute, nodeRouteDest);
            nodeRouteDest.setCreateTime(new Date());
            return new ResponseBean(nodeRouteService.add(nodeRouteDest));
        } catch (Exception e) {
            logger.error("\n添加路由配置失败:", e);
            return new ResponseBean("添加路由配置失败");
        }
    }

    @ApiOperation(value = "删除路由配置", notes = "删除路由配置")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseBean delete(@RequestBody List<NodeRoute> nodeRouteList) {
        try {

            if (nodeRouteList == null || nodeRouteList.isEmpty()) {
                return new ResponseBean("缺少参数");
            }

            for (NodeRoute nodeRoute : nodeRouteList) {
                if (StringUtils.isEmpty(nodeRoute.getLeftNodeId())
                        || StringUtils.isEmpty(nodeRoute.getRightNodeId())) {
                    return new ResponseBean("缺少参数");
                }

                Integer count = nodeRouteService.selectLinkCountByNodeRoute(nodeRoute);
                if (count > 0) {
                    NodeRouteVo nodeRouteVo = nodeRouteService.selectByPrimarykey(nodeRoute.getLeftNodeId(), nodeRoute.getRightNodeId());
                    return new ResponseBean("路由(routeName="+nodeRouteVo.getRouteName()+",leftNodeId="+nodeRouteVo.getLeftNodeId()+",rightNodeId="+nodeRouteVo.getRightNodeId()+")已被引用，不能删除");
                }
            }

            nodeRouteService.deleteList(nodeRouteList);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n添加路由配置失败:", e);
            return new ResponseBean("添加路由配置失败");
        }
    }

    @ApiOperation(value = "编辑路由配置", notes = "编辑路由配置")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseBean update(@RequestBody NodeRouteDto nodeRoute) {
        try {
            if (StringUtils.isEmpty(nodeRoute.getLeftNodeId())
                    || StringUtils.isEmpty(nodeRoute.getRightNodeId())) {
                return new ResponseBean("缺少参数");
            }

            NodeRoute nodeRouteDest = new NodeRoute();
            BeanUtils.copyProperties(nodeRoute, nodeRouteDest);

            return new ResponseBean(nodeRouteService.update(nodeRouteDest));
        } catch (Exception e) {
            logger.error("\n编辑路由配置失败:", e);
            return new ResponseBean("编辑路由配置失败");
        }
    }
}
