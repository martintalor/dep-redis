package com.iflytek.dep.common.pack;


import com.iflytek.dep.common.utils.CommonConstants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.pack
 * @Description:
 * @date 2019/2/20--15:49
 */
public class Zip4JUtil {


    /**
     * @描述 压缩文件夹
     * @参数 [srcDir, outDir, fromTo]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/20
     * @修改人和其它信息
     */
    public static void toZip(String srcDir, String outDir, String fileName) throws ZipException {
        long start = System.currentTimeMillis();
        //生成压缩后文件存放路径
        Map outResult = FileUtil.makeZipOut(outDir, fileName);
        String outStr = null;
        if ((boolean) outResult.get("flag")) {
            outStr = String.valueOf(outResult.get("path"));
        }
        ZipFile zipFile = new ZipFile(outStr + CommonConstants.FILE_INFO.FILESPLIT + fileName + CommonConstants.FILE_INFO.ZIP);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        zipFile.addFolder(srcDir, parameters);
        long end = System.currentTimeMillis();
        System.out.println("压缩完成，耗时：" + (end - start) + " ms");

    }

    /**
     * @描述 将文件夹进行分卷压缩
     * @参数 [outStr 目标文件夹, srcFolder需压缩文件夹, unit 分卷大小 单位是gb, fileNames 名称]
     * @返回值 java.lang.String 返回zip完整路径
     * @创建人 朱一帆
     * @创建时间 2019/2/20
     * @修改人和其它信息
     */
    // 将文件夹进行分卷压缩
    public static String zipTOSplit(String outStr, String srcFolder, int unit, String fileNames)
            throws ZipException {
        long start = System.currentTimeMillis();
        System.out.println("分卷压缩开始");
        //压缩主文件全路径
        String fileName = outStr + CommonConstants.FILE_INFO.FILESPLIT + fileNames + CommonConstants.FILE_INFO.ZIP;
        ZipFile zip = new ZipFile(fileName);
        //设置文件名称编码方式
        zip.setFileNameCharset("UTF-8");
        ZipParameters para = new ZipParameters();
        // 默认COMP_DEFLATE
        para.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        para.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        // 设置分卷大小单位转换为gb
        //long split = unit * 1024l * 1024l * 1024l;
        long split = unit * 1024l * 1024l;
        //分卷压缩文件夹到指定压缩位置
        zip.createZipFileFromFolder(srcFolder, para, true, split);
        long end = System.currentTimeMillis();
        System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        //返回完整路径的压缩包主文件路径
        return fileName;

    }

    /**
     * @描述 预览压缩文件信息
     * @参数 [zipFile]
     * @返回值 double
     * @创建人 朱一帆
     * @创建时间 2019/2/21
     * @修改人和其它信息
     */
    // 预览压缩文件信息
    public static double zipInfo(String zipFile) throws ZipException {
        ZipFile zip = new ZipFile(zipFile);
        zip.setFileNameCharset("UTF-8");
        List<FileHeader> list = zip.getFileHeaders();
        long zipCompressedSize = 0;
        for (FileHeader head : list) {
            zipCompressedSize += head.getCompressedSize();
//   System.out.println(zipFile+"文件相关信息如下：");
//   System.out.println("Name: "+head.getFileName());
//   System.out.println("Compressed Size:"+(head.getCompressedSize()/1.0/1024)+"kb");
//   System.out.println("Uncompressed Size:"+(head.getUncompressedSize()/1.0/1024)+"kb");
//   System.out.println("CRC32:"+head.getCrc32());
//   System.out.println("*************************************");
        }
        double size = zipCompressedSize / 1.0 / 1024;//转换为kb
        return size;
    }

    /**
     * @描述解压方法
     * @参数 [zipFile zip完整路径]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/21
     * @修改人和其它信息
     */
    // 解压方法
    public static void Unzip4j(String file) throws ZipException {
        System.out.println("解压开始");
        boolean valid = isValid(file);
        if (valid) {
            ZipFile zipFile = new ZipFile(file);
            //第一时间设置编码格式
            zipFile.setFileNameCharset("UTF-8");
            String parentPath = new File(file).getParent();
            int fileNum = new File(parentPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(new File(name).isDirectory()){
                        return false;
                    }
                    return true;
                }
            }).length;

            valid = (zipFile.getSplitZipFiles().size() == fileNum);
        }
        if(!valid)
        {
            throw new ZipException("zip文件不全");
        }

        long startTime = System.currentTimeMillis();
        ZipFile zip = new ZipFile(file);
        //第一时间设置编码格式
        zip.setFileNameCharset("UTF-8");
        //用自带的方法检测一下zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
        if (!zip.isValidZipFile()) {
            throw new ZipException("文件不合法或不存在");
        }
        //checkEncrypted(zip);
        // 跟java自带相比，这里文件路径会自动生成，不用判断
        zip.extractAll(zip.getFile().getParent());
        System.out.println("解压成功！");
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) + "ms");
    }


    /**
     * @描述 检测密码
     * @参数 [zip  文件检测是否需要密码]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/21
     * @修改人和其它信息
     */
    private static void checkEncrypted(ZipFile zip) throws ZipException {
        Scanner in = new Scanner(System.in);
        if (zip.isEncrypted()) {
            System.out.println("文件" + zip.getFile().getName() + "有密码！");
            System.out.println("请输入密码：");
            zip.setPassword(in.next().trim());
        }
        in.close();
    }

    //验证压缩包的完整性
    public static boolean isValid(String path) throws ZipException {
        ZipFile zip = new ZipFile(path);
        //第一时间设置编码格式
        zip.setFileNameCharset("UTF-8");
        //用自带的方法检测一下zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
        return zip.isValidZipFile();
    }

}
