package per.zh.tess4j.RSA;

import java.io.*;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     *
     * @return 返回文件所在路径
     */
    public static String getBasePath() {
        //获取文件路径
        String filePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8"); //解决路径中有空格%20的问题
            } catch (UnsupportedEncodingException ex) {

            }
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 将Byte数组转换成文件
     *
     * @param bytes    byte数组
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return 文件生成成功返回 true 失败返回 false
     */
    public static boolean getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
