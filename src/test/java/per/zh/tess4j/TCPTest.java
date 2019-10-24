package per.zh.tess4j;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPTest {

    @Test
    public void test() {
        try {
            //1.创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket("172.16.1.162", 502);

            //3.获取输入流，并读取服务器端的响应信息
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("接收温湿度传感器数据：" + info);
            }
            //4.关闭资源
            br.close();
            is.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
