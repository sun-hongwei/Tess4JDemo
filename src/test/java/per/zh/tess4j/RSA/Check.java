package per.zh.tess4j.RSA;

import javax.crypto.Cipher;
import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import sun.misc.BASE64Decoder;

/**
 * 利用 classLoader加载字节码文件进行验证
 *
 * @version 1.0
 */
public class Check {

    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";


    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String hello(){

        return "hello";
    }

    /**
     * 机器码验证
     *
     * @param serialNumber 解密的机器码
     * @return
     */
    public static boolean checkSerialNumber(String serialNumber, byte[] file, String publicKey) {
        //获取本机机器码
        String localSerialNumber = getAllSn().get("serialNumber");
        if (isNotEmpty(localSerialNumber) && isNotEmpty(serialNumber) && file != null) {
            try {
                //公钥解密机器码
                byte[] decodedData = decryptByPublicKey(file, publicKey);
                //解密结果
                String target = new String(decodedData);
                if (isNotEmpty(target)) {
                    //3、对比验证
                    return serialNumber.equals(target);
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /***************************linux*********************************/

    public static String executeLinuxCmd(String cmd) {
        try {
            Runtime run = Runtime.getRuntime();
            Process process;
            process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            in.close();
            process.destroy();
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param cmd    命令语句
     * @param record 要查看的字段
     * @param symbol 分隔符
     * @return
     */
    public static String getSerialNumber(String cmd, String record, String symbol) {
        String execResult = executeLinuxCmd(cmd);
        String[] infos = execResult.split("\n");

        for (String info : infos) {
            info = info.trim();
            if (info.indexOf(record) != -1) {
                info.replace(" ", "");
                String[] sn = info.split(symbol);
                return sn[1];
            }
        }
        return null;
    }

    /**
     * 获取CPUID、硬盘序列号、MAC地址、主板序列号
     *
     * @return
     */
    public static Map<String, String> getAllSn() {
        //获取操作系统
        String os = System.getProperty("os.name");
        os = os.toUpperCase();

        Map<String, String> snVo = new HashMap<String, String>();

        if (os.contains("LINUX")) {
            String cpuid = getSerialNumber("dmidecode -t processor | grep 'ID'", "ID", ":");
            String mainboardNumber = getSerialNumber("dmidecode |grep 'Serial Number'", "Serial Number", ":");
            //备用字段
            //String diskNumber = getSerialNumber("fdisk -l", "Disk identifier", ":");

            //cpuId加主板ID
            if (cpuid != "" && mainboardNumber != "") {
                String serialNumber = cpuid + mainboardNumber;
                snVo.put("serialNumber", serialNumber.trim());
            }
        } else if (os.contains("WINDOWS")) {
            System.out.println("=============>for windows");
            String cpuid = getCPUSerial();
            String mainboard = getMotherboardSN();

            //cpuId加主板ID
            if (cpuid != "" && mainboard != "") {
                String serialNumber = cpuid + mainboard;
                snVo.put("serialNumber", serialNumber.trim());
            }
        } else if (os.contains("MAC OS X")) {
            String serialNumber = getSerialNumber("system_profiler SPHardwareDataType", "Serial Number (system)", ":");
            if (serialNumber != "") {
                snVo.put("serialNumber", serialNumber.trim());
            }
        }
        return snVo;
    }

    /** */
    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 判断是否为空字符串最优代码
     *
     * @param str
     * @return 如果为空，则返回true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 如果不为空，则返回true
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** */
    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return new BASE64Decoder().decodeBuffer(base64);
    }

    /**
     * 获取CPU序列号
     *
     * @return
     */
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";
            fw.write(vbs);
            fw.close();
            String path = file.getPath().replace("%20", " ");
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + path);
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }

    /**
     * 获取主板序列号
     */
    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            String path = file.getPath().replace("%20", " ");
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + path);
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

}
