package per.zh.tess4j.RSA;

import org.junit.Test;
import per.zh.tess4j.system.SerialNumberUtil;

import java.io.*;
import java.util.Map;

/**
 * 生成license
 */
public class LicenseGenerator {

    /**
     * 获取机器码
     */
    private static String serialNumber = SerialNumberUtil.getAllSn().get("serialNumber");

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrAs5CrInJaTtEP+Ar/WRV0mgxd9qW/geAY/jj\n" +
            "Kbsy9IcQ7eHQm8v78yl7y/HnjLUsESGzZoTD1rplBxJHt1nAzlhsUOF++i43kadIv9QPrbi/+aj6\n" +
            "p0djwdp+JIxoTSq/H76wj3tieDDjc8IEKmsA1j1hOQt7O/P7EBsyCElr+wIDAQAB";

    /**
     * RSA算法
     * 公钥和私钥是一对，此处只用私钥加密
     */
    public static final String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKsCzkKsiclpO0Q/4Cv9ZFXSaDF3\n" +
            "2pb+B4Bj+OMpuzL0hxDt4dCby/vzKXvL8eeMtSwRIbNmhMPWumUHEke3WcDOWGxQ4X76LjeRp0i/\n" +
            "1A+tuL/5qPqnR2PB2n4kjGhNKr8fvrCPe2J4MONzwgQqawDWPWE5C3s78/sQGzIISWv7AgMBAAEC\n" +
            "gYBSvv3Mty3InjmGMZ4nyukkubWe0g6b5vj8P+NrD4M8CaPdJi2QwgH2vFcDRtCbKLN1SijTZLSz\n" +
            "ChCQ19YZi3dKkBC5JWophPLBM5VV2MaDdhrRmwU/uWG/k59rN+r+erllF0EYGZJvhs2fvRY+mluF\n" +
            "8DUIMoVsltgChF+BORVwIQJBANv39Po5/wBZ5EydrLByQHsnx62QkjKTurl+tZykHt+A/OEW7mcs\n" +
            "KBvq6Pa4iWhhLDgstBk6ckS5bsL0qmfy/DECQQDHBeLsztLV2KKPHkfmLM8OJ7vbT1aRvHnqiYil\n" +
            "sdcFeaVOY04tOslcrQAkTLzICysSiIHbavflvvswEErM3tvrAkBmkX1SvH5STJguvYsLj4rPomHy\n" +
            "Zao2GTqFSjcm/kRDFaNU1dt+klF1ixjsCmpQGJeE7htgdCdaMlkaYPmglOXRAkA0SaWe1Y+qwymi\n" +
            "NDBrlqYnb+T471UtILlfFxchgSGevKDjgU+Orn0srf71inumiticG+USAn5yi8Rukjk0rfBVAkB6\n" +
            "7IGmCvGzKZovisQ/4tZpcUEU20fUSbbtu3L0coQ9URV61OFBZseQVWdsfjfE4rV8a06+PHAVwySJ\n" +
            "KOaWW4pt";

    /**
     * 私钥加密机器码，生成license 文件
     *
     * @throws Exception
     */
    public static void generator() throws Exception {
        System.err.println("私钥加密——公钥解密");
        System.out.println("机器码：\r\n" + serialNumber);
        if (StringUtils.isNotEmpty(serialNumber)) {
            byte[] data = serialNumber.getBytes();
            //私钥加密
            byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
            System.out.println("加密后：\r\n" + new String(encodedData)); //加密后乱码是正常的

            //生成license文件
            Base64Utils.byteArrayToFile(encodedData, FileUtil.getBasePath() + File.separator + "license.dat");
            System.out.println("license.dat：\r\n" + FileUtil.getBasePath() + File.separator + "license.dat");
        }
    }

    /**
     * 生成私钥/公钥密码对
     */
    @Test
    public void getPriPubKey() {
        try {
            //生成密钥对(公钥和私钥)
            Map<String, Object> keyMap = RSAUtils.genKeyPair();
            String publicKey = RSAUtils.getPublicKey(keyMap);
            String privateKey = RSAUtils.getPrivateKey(keyMap);
            System.err.println("公钥: \n\r" + publicKey);
            System.err.println("私钥： \n\r" + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密机器码，生成license文件
     */
    @Test
    public void generateLicense() {
        try {
            generator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据生成的license 进行解密
     */
    @Test
    public void decodingLicense() {
        //1、获取License文件路径
        String licensePath = FileUtil.getBasePath() + "/license.dat";

        if (StringUtils.isNotEmpty(licensePath)) {
            //读取文件内容
            byte[] bytes = readToByte(licensePath);
            if (bytes != null) {
                try {
                    //2、公钥解密
                    byte[] decodedData = RSAUtils.decryptByPublicKey(bytes, publicKey);
                    //解密结果
                    String target = new String(decodedData);
                    //3、对比验证
                    if (StringUtils.isNotEmpty(target)) {
                        if (target.equals(serialNumber)) {
                            System.out.println("验证通过");
                        } else {
                            System.out.println("验证失败，请联系长春易加科技有限公司进行系统注册");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("验证失败，请联系长春易加科技有限公司进行系统注册");
                }
            }
        }
    }

    /**
     * 读文件
     *
     * @param fileName License文件路径
     * @return
     */
    private byte[] readToByte(String fileName) {
        //byte 文件内容
        byte[] fileContent = null;
        File file = new File(fileName);
        if (file.exists()) {
            try {
                Long fileLength = file.length();
                fileContent = new byte[fileLength.intValue()];
                FileInputStream in = new FileInputStream(file);
                in.read(fileContent);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }
}