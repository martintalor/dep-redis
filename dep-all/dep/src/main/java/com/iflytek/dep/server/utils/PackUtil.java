package com.iflytek.dep.server.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/3/1--10:00
 */
public class PackUtil {

    /**
     * @描述 splitAppTo
     * @参数 [packId]
     * @返回值 java.lang.String[]
     * @创建人 朱一帆
     * @创建时间 2019/3/1
     * @修改人和其它信息
     */
    public static String[] splitAppTo(String packId) {
        //只解析我们规定的PKG_XXX_TO_XXX,XXX,XXX_时间戳_uuid这样的包名
        String[] split = packId.split(CommonConstants.NAME.SPILT);
        String[] appTo = split[3].split(CommonConstants.NAME.APPSPLIT);
        return appTo;
    }

    /**
     *@描述  splitAppFrom
     *@参数  [packId]
     *@返回值  java.lang.String
     *@创建人  朱一帆
     *@创建时间  2019/3/4
     *@修改人和其它信息
     */
    public static String splitAppFrom(String packId) {
        //只解析我们规定的PKG_XXX_TO_XXX,XXX,XXX_时间戳_uuid这样的包名

        String[] split = packId.split(CommonConstants.NAME.SPILT);
        return split[1];
    }

    public static void main(String[] args) {
        String s = splitAppFrom("PKG_APP-G1_TO_APP-J1_1561299066159_14392ca250e2454f988a92ac5e831909#9");
        System.out.println( s );
    }

    public static String splitAppTos(String packId) {
        //只解析我们规定的PKG_XXX_TO_XXX,XXX,XXX_时间戳_uuid这样的包名
        String[] split = packId.split(CommonConstants.NAME.SPILT);
        return split[3];
    }


    /**
     * @描述 deleteZip 根据主包路径删除同一批压缩包
     * @参数 [path]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/1
     * @修改人和其它信息
     */
    public static void deleteZips(String path) {
        int a = 1;
        //删除同一批压缩包
        while (true) {
            File zip = new File(path);
            if (zip.exists()) {
                zip.delete();
            } else {
                return;
            }
            String b = null;
            if (a < 10) {
                b = "0" + String.valueOf(a);
            } else {
                b = String.valueOf(a);
            }
            path = path.substring(0, path.length() - 2) + b;
            a++;
        }
    }

    /**
     *@描述  deleteZip
     *@参数  [path]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/6
     *@修改人和其它信息
     */
    public static void deleteZip(String path){
        File zip = new File(path);
        if (zip.exists()) {
            zip.delete();
        }
    }

    /**
     * @描述 renameFile 文件重命名
     * @参数 [path 文件目录, oldname 原来的文件名, newname 新文件名]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/1
     * @修改人和其它信息
     */
    public static void renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(path + "/" + oldname);
            File newfile = new File(path + "/" + newname);
            if (!oldfile.exists()) {
                return;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }
    }


    /**
     * @描述 renameAndDeleteZip
     * @参数 [path]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/1
     * @修改人和其它信息
     */
    //当加密文件解密完放在同一个文件夹下时需要的操作
    public static void renameAndDeleteZip(String path) {
        File zip = new File(path);
        if (zip.exists()) {
            String parent = zip.getParent();
            String newName = zip.getName();
            zip.delete();
            renameFile(parent, "[decrypt]" + newName, newName);
        } else {
            return;
        }

    }

    /**
     *@描述  renameAndDeleteLeafZip
     *@参数  [path]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/27
     *@修改人和其它信息
     */
    //解密后操作
    public static void renameAndDeleteLeafZip(String path) {
        File zip = new File(path);
        if (zip.exists()) {
            String parent = zip.getParent();
            String newName = zip.getName();
            zip.delete();
            renameFile(parent+ CommonConstants.NAME.FILESPLIT+"unpack", "[decrypt]" + newName, newName);
        } else {
            return;
        }

    }

    /**
     *@描述  makeDir 根据路径创建文件夹
     *@参数  [path]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/5
     *@修改人和其它信息
     */
    public static void makeDir(String path){
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    /**
     *@描述  isValid
     *@参数  [path 判断包完整的包的全路径]
     *@返回值  boolean
     *@创建人  朱一帆
     *@创建时间  2019/3/6
     *@修改人和其它信息
     */
    //验证压缩包的完整性
    public static boolean isValid(String path) throws ZipException {
        ZipFile zip = new ZipFile(path);
        //第一时间设置编码格式
        zip.setFileNameCharset("UTF-8");
        //用自带的方法检测一下zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
        return zip.isValidZipFile();
    }

    /**
     * @描述 subStringAck
     * @参数 [packageId 去掉后缀的包名]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/6/6
     * @修改人和其它信息
     */
    public static String subStringAck(String packageId) {

        if (packageId != null) {
            return packageId.substring(0,packageId.indexOf("ACK"));
        }else{
            return null;
        }
    }


    public static boolean verifyZip(String file) throws Exception {
        if(file==null)
        {
            return true;
        }

        boolean valid = isValid(file);
        if (valid) {
            ZipFile zipFile = new ZipFile(file);
            //第一时间设置编码格式
            zipFile.setFileNameCharset("UTF-8");
            String parentPath = new File(file).getParent();
            int fileNum = new File(parentPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(new File(dir + File.separator + name).isDirectory()){
                        return false;
                    }
                    return true;
                }
            }).length;

            valid = (zipFile.getSplitZipFiles().size() == fileNum);
        }
        if(!valid)
        {

        }

        return valid;
    }
}
