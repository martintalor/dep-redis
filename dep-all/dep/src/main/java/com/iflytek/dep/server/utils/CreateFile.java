package com.iflytek.dep.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/3/14--21:55
 */
public class CreateFile
{
//    public static void main(String[] args) throws IOException
//    {
//        String filePath = "D:\\temp\\api-auto-test\\10000-files";
//        File file = new File(filePath);
//        if(!file.exists()){
//            file.mkdirs();
//        }
//        long start = System.currentTimeMillis();
//        for(int i=0;i<10;i++){
//            String fileName = "auto-test1"+i;
//            File f = new File(filePath,fileName);
//            if(!f.exists()){
//                createFile(f,1024l*1024l);
//            }
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("total times "+(end-start));
//        start = System.currentTimeMillis();
//        for(int i=0;i<10;i++){
//            String fileName = "auto-test2"+i;
//            File f = new File(filePath,fileName);
//            if(!f.exists()){
//                createFixLengthFile(f,10l);
//            }
//        }
//        end = System.currentTimeMillis();
//        System.out.println("total times "+(end-start));
//    }

    public static void createFixLengthFile(File file, long length) throws IOException{
        FileOutputStream fos = null;
        FileChannel output = null;
        try {
            fos = new FileOutputStream(file);
            output = fos.getChannel();
            output.write(ByteBuffer.allocate(1), length-1);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFile(File file, long length) throws IOException {
        RandomAccessFile ff = null;
        try{
            ff = new RandomAccessFile(file,"rw");
            ff.setLength(length);
        }finally{
            if (ff != null){
                try{
                    ff.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
