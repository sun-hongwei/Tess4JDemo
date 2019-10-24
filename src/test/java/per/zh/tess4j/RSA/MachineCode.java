package per.zh.tess4j.RSA;

import per.zh.tess4j.system.SerialNumberUtil;
import java.util.Map;

/**
 * 获取机器码
 */
public class MachineCode {

    public static void main(String[] args) {
        try {
            //测试获取机器码
            Map<String, String> allSn = SerialNumberUtil.getAllSn();
            System.out.println(allSn.get("serialNumber"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
