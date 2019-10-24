package per.zh.tess4j;

public class TestClass {

    /**
     * 数字转16进制
     */
    @org.junit.Test
    public void testIntegerToHex(){
        int i = 10;

        System.out.println(Integer.toHexString(i));
    }

    /**
     * 获取操作系统
     */
    @org.junit.Test
    public void getOS(){
        String os = System.getProperty("os.name");
        os = os.toUpperCase();
        System.out.println(os);
    }

    @org.junit.Test
    public void testEquals(){

        String i = "MAC OS X";

        System.out.println("MAC OS X".equals(i));

        System.out.println(i.contains("OS"));

    }

}
