package com.iflytek.dep.admin.controller;

import static com.iflytek.dep.admin.utils.CommonConstants.LOGIN_INFO.SESSION_KEY;

import javax.servlet.http.HttpServletRequest;

import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.service.LocalServerNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iflytek.dep.admin.model.vo.UserLoginParam;
import com.iflytek.dep.common.utils.Md5Util;
import com.iflytek.dep.common.utils.RandomGUID;
import com.iflytek.dep.common.utils.ResponseBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录接口
 */
@Api(value="业务系统接口类",tags={"数据交换平台与业务系统接口类"})
@RestController
public class LoginController {

    @Autowired
    private LocalServerNodeService localServerNodeService;

    @Autowired
    private Environment environment;

    @ApiOperation(value = "用户登录接口",notes="用户登录接口")
    @PostMapping("/login")
    public ResponseBean<Map<String, Object>> userLogin(@RequestBody UserLoginParam userLoginParam, HttpServletRequest request){
        ResponseBean<Map<String, Object>> result = new ResponseBean<>();
        Map<String, Object> resultMap = new HashMap<>();
        String propertyUserName = environment.getProperty("login.security.user.name");
        String propertyPassWord = environment.getProperty("login.security.user.password");
        if(propertyPassWord.equals(Md5Util.md5(userLoginParam.getPassWord()))&&propertyUserName.equals(userLoginParam.getUserName())){

            //查询当前逻辑节点信息，并返回给前端
            String serverNodeId = environment.getProperty("logicServerNode.serverNodeId");
            if (!StringUtils.isEmpty(serverNodeId)) {
                LocalServerNode localServerNode = localServerNodeService.selectByServerNodeId(serverNodeId);
                resultMap.put("localServerNode", localServerNode);
            }

            resultMap.put("result", "true");

            result.setRows(resultMap);
			result.setMessage("登录成功");
			String guid = RandomGUID.getGuid();
            request.getSession().setAttribute(SESSION_KEY,guid);
            //负数或者0为永远不超时
            request.getSession().setMaxInactiveInterval(-1);
            return result;
        }else{
            resultMap.put("result", "true");

            result.setRows(resultMap);
            result.setMessage("用户名密码错误");
            return result;
        }
    }

    @ApiOperation(value = "用户登出接口",notes="用户登出接口")
    @PostMapping("/logout")
    public ResponseBean<Boolean> userLogout(HttpServletRequest request){
        ResponseBean<Boolean> result = new ResponseBean<>();
        request.getSession().removeAttribute(SESSION_KEY);
        result.setRows(true);
        result.setMessage("退出成功");

         return result;
    }
}
