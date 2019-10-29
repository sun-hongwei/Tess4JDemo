package per.zh.tess4j.RSA;

import org.junit.Test;
import per.zh.tess4j.system.SerialNumberUtil;

import java.io.*;
import java.lang.reflect.Method;
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
     * 私钥加密代码与机器码，生成license 文件
     */
    @Test
    public void generator() {
        if (StringUtils.isNotEmpty(serialNumber)) {
            try {
                //代码
                byte[] data1 = Base64Utils.fileToByte("/Users/hongweisun/share/Check.class");
                //机器码
                byte[] data2 = serialNumber.getBytes();

                //1、私钥加密（代码）
                byte[] classCode = RSAUtils.encryptByPrivateKey(data1, privateKey);
                //2、生成代码license文件
                Base64Utils.byteArrayToFile(classCode, FileUtil.getBasePath() + File.separator + "license1.dat");

                //3、私钥加密（机器码）
                byte[] encodedData = RSAUtils.encryptByPrivateKey(data2, privateKey);
                //4、生成机器码license文件
                Base64Utils.byteArrayToFile(encodedData, FileUtil.getBasePath() + File.separator + "license2.dat");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
     * 根据生成的license 进行解密
     */
    @Test
    public void decodingLicense() {
        //1、获取代码License文件路径
        String codeLicensePath = FileUtil.getBasePath() + File.separator + "license1.dat";

        //2、获取机器码License文件路径
        String serialNumberLicensePath = FileUtil.getBasePath() + File.separator + "license2.dat";

        if (StringUtils.isNotEmpty(codeLicensePath) && StringUtils.isNotEmpty(serialNumberLicensePath)) {
            try {
                //读取代码文件内容
                byte[] codeBytes = Base64Utils.fileToByte(codeLicensePath);
                //公钥解密代码文件
                byte[] decodedData = RSAUtils.decryptByPublicKey(codeBytes, publicKey);
                //将byte数组转文件
                boolean fileStatus = FileUtil.getFileByBytes(decodedData, FileUtil.getBasePath(), "Check.dat");
                //读取机器码文件内容
                byte[] serialNumberBytes = Base64Utils.fileToByte(serialNumberLicensePath);
                if (fileStatus && serialNumberBytes != null) {
                    //自定义classLoader
                    ByteLoader loader = new ByteLoader();
                    File file = new File(FileUtil.getBasePath() + File.separator + "Check.dat");

                    try (FileInputStream in = new FileInputStream(file)) {
                        byte[] bytes = new byte[in.available()];
                        in.read(bytes);
                        loader.addByte("Check", bytes);
                    }
                    //反射
                    Class<?> clazz = loader.findClass("Check");

                    Method method = clazz.getMethod("checkSerialNumber", String.class, byte[].class, String.class);

                    Object invoke = method.invoke(clazz.newInstance(), serialNumber, serialNumberBytes, publicKey);

                    System.out.println(invoke);
                } else {
                    System.out.println("注册失败，请联系长春易加科技有限公司。客服电话：4007-1617-67");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 测试classLoader
     */
    @Test
    public void loadClass() {
        try {
            //自定义classLoader
            ByteLoader loader = new ByteLoader();
            File file = new File("/Users/hongweisun/JavaDev/Tess4JDemo/target/test-classes/Check.dat");

            try (FileInputStream in = new FileInputStream(file)) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                loader.addByte("Check", bytes);
            }
            Class<?> clazz = loader.findClass("Check");

            Method method = clazz.getMethod("getSerialNumber");

            Object invoke = method.invoke(clazz.newInstance());

            System.out.println(invoke);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}