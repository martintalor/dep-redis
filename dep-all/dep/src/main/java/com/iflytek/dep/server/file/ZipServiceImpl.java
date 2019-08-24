package com.iflytek.dep.server.file;


import com.iflytek.dep.common.pack.Zip4JUtil;
import com.iflytek.dep.server.utils.FileConfigUtil;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.stereotype.Service;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.service.impl
 * @Description:
 * @date 2019/2/22--20:36
 */
@Service
public class ZipServiceImpl implements ZipService {

    /**
     *@描述  toZip
     *@参数  [packDirPath, fileName]
     *@返回值  java.lang.String
     *@创建人  朱一帆
     *@创建时间  2019/2/25
     *@修改人和其它信息
     */
    @Override
    public String toZip(String outStr,String packDirPath,String fileName) throws ZipException {
        //分卷大小
        int shuntSize = FileConfigUtil.SHUNTSIZE;
        String path = Zip4JUtil.zipTOSplit(outStr, packDirPath, shuntSize, fileName);
        return path;
    }

    /**
     *@描述  unZip
     *@参数  [packedPath]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/2/25
     *@修改人和其它信息
     */
    @Override
    public void unZip(String packedPath) throws ZipException {
        Zip4JUtil.Unzip4j(packedPath);
    }
}
