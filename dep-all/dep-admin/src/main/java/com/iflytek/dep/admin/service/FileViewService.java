package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.vo.DirectoryVo;
import com.iflytek.dep.admin.model.vo.FTPConfigVo;
import com.iflytek.dep.admin.model.vo.FileVo;

import java.io.IOException;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description: FileViewService
 * @date 2019/3/6--14:53
 */
public interface FileViewService {

    List<FTPConfigVo> listFTP(String serverNodeId);

    List<DirectoryVo> listDirectories(String ftpId) throws IOException;

    List<FileVo> listFiles(String ftpId, String directory) throws IOException;
}
