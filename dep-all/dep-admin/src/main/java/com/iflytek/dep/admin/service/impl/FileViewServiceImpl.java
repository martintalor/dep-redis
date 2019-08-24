package com.iflytek.dep.admin.service.impl;

import com.iflytek.dep.admin.dao.FTPConfigMapper;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.vo.DirectoryLevelVo;
import com.iflytek.dep.admin.model.vo.DirectoryVo;
import com.iflytek.dep.admin.model.vo.FTPConfigVo;
import com.iflytek.dep.admin.model.vo.FileVo;
import com.iflytek.dep.admin.service.FileViewService;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.common.utils.UUIDGenerator;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description: FileViewServiceImpl
 * @date 2019/3/6--14:57
 */
@Service
public class FileViewServiceImpl implements FileViewService {

    Logger logger = LoggerFactory.getLogger(FileViewServiceImpl.class);

    @Autowired
    private FTPConfigMapper ftpConfigMapper;

    @Override
    public List<FTPConfigVo> listFTP(String serverNodeId) {
        return ftpConfigMapper.selectByServerNodeId(serverNodeId);
    }

    @Override
    public List<DirectoryVo> listDirectories(String ftpId) throws IOException {
        FTPConfig ftpConfig = ftpConfigMapper.selectByPrimaryKey(ftpId);
        if (null == ftpConfig) {
            return null;
        }
        FTPClient ftp = connectFTP(ftpConfig.getFtpIp(), ftpConfig.getFtpPort(), ftpConfig.getUsername(), ftpConfig.getPassword());
        // 存储ftp所有的目录信息
        List<DirectoryLevelVo> directoryLevelVos = new ArrayList<DirectoryLevelVo>();
        getDirectory(directoryLevelVos, ftp, "0", 0, CommonConstants.NAME.FILESPLIT);
        if (CollectionUtils.isEmpty(directoryLevelVos)) {
            return null;
        }
        // 构建目录信息的树数据结构
        List<DirectoryVo> nodes = new ArrayList<>();
        for (int i = 0; i < directoryLevelVos.size(); i++) {
            if (directoryLevelVos.get(i).getParentId().equals("0")) {
                DirectoryVo node = generateTree(directoryLevelVos.get(i).getId(), directoryLevelVos);
                nodes.add(node);
            }
        }
        return nodes;
    }


    @Override
    public List<FileVo> listFiles(String ftpId, String directory) throws IOException {
        FTPConfig ftpConfig = ftpConfigMapper.selectByPrimaryKey(ftpId);
        if (null == ftpConfig) {
            return null;
        }
        FTPClient ftp = connectFTP(ftpConfig.getFtpIp(), ftpConfig.getFtpPort(), ftpConfig.getUsername(), ftpConfig.getPassword());
        ftp.changeWorkingDirectory(directory);
        FTPFile[] files = ftp.listFiles();
        if (files != null) {
            List<FileVo> fileVos = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    fileVos.add(new FileVo(i, files[i].getName(), files[i].getSize(), files[i].getTimestamp().getTime()));
                }
            }
            return fileVos;
        }
        return null;
    }

    /**
     * 连接ftp
     *
     * @param ftpIp
     * @param ftpPort
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    private FTPClient connectFTP(String ftpIp, int ftpPort, String username, String password) throws IOException {
        FTPClient ftp = new FTPClient();
        ftp.connect(ftpIp, ftpPort);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            logger.error("FTPServer 拒绝连接");
        }
        boolean result = ftp.login(username, password);
        if (!result) {
            logger.error("ftpClient登录失败! userName:" + username + ", password:" + password);
        }
        // 设置ftp连接为被动模式
        ftp.enterLocalPassiveMode();
        return ftp;
    }

    /**
     * 递归获取所有目录
     *
     * @param list
     * @param ftp
     * @param parentId 父节点
     * @param sort     排序
     * @throws IOException
     */
    private void getDirectory(List<DirectoryLevelVo> list, FTPClient ftp, String parentId, int sort, String dir) throws IOException {
        // 替换目录
        ftp.changeWorkingDirectory(dir);
        // logger.info("当前ftp路径{}，文件目录{}", ftp.printWorkingDirectory(), ftp.listDirectories());
        FTPFile[] directories = ftp.listDirectories();
        if (directories != null) {
            for (int i = 0; i < directories.length; i++) {
                String id = UUIDGenerator.createUUID();
                String path = null;
                if (dir.endsWith(CommonConstants.NAME.FILESPLIT)) {
                    path = dir + directories[i].getName();
                } else {
                    path = dir + CommonConstants.NAME.FILESPLIT + directories[i].getName();
                }
                list.add(new DirectoryLevelVo(id, directories[i].getName(), parentId, i, path));
                // 递归
                getDirectory(list, ftp, id, 0, path);

            }
        }
    }

    /**
     * 生成树结构信息
     *
     * @param id
     * @param directoryLevelVos
     * @return
     */
    private DirectoryVo generateTree(String id, List<DirectoryLevelVo> directoryLevelVos) {
        DirectoryVo directoryVo = getNodeById(id, directoryLevelVos);
        List<DirectoryVo> childTreeNodes = getChildNodeByPid(id, directoryLevelVos);
        // 遍历子节点
        for (DirectoryVo tn : childTreeNodes) {
            DirectoryVo n = generateTree(tn.getId(), directoryLevelVos); // 递归
            if (null != directoryVo) {
                directoryVo.getChildren().add(n);
            }
        }
        return directoryVo;
    }

    /**
     * 根据父节点id取子节点列表并排序
     *
     * @param id
     * @param directoryLevelVos
     * @return
     */
    private List<DirectoryVo> getChildNodeByPid(String id, List<DirectoryLevelVo> directoryLevelVos) {
        List<DirectoryVo> list = new ArrayList<>();
        for (DirectoryLevelVo dto : directoryLevelVos) {
            if (dto.getParentId().equals(id)) {
                DirectoryVo node = new DirectoryVo(dto.getId(), dto.getDirectoryName(), dto.getSort(), dto.getDir());
                list.add(node);
            }
        }
        Collections.sort(list, Comparator.comparingInt(DirectoryVo::getSort));
        return list;
    }

    /**
     * 根据id取节点
     *
     * @param id
     * @param directoryLevelVos
     * @return
     */
    private DirectoryVo getNodeById(String id, List<DirectoryLevelVo> directoryLevelVos) {
        for (DirectoryLevelVo dto : directoryLevelVos) {
            if (dto.getId() == id) {
                DirectoryVo node = new DirectoryVo(dto.getId(), dto.getDirectoryName(), dto.getSort(), dto.getDir());
                return node;
            }
        }
        return null;
    }

    /**
     * 转码
     *
     * @param str
     * @return
     */
    private String changeCoding(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes("GBK"), "iso-8859-1");
    }
}