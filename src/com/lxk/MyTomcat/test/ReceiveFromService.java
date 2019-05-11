package com.lxk.MyTomcat.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReceiveFromService {

    public static void main(String[] args) {
	// write your code here
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
//            http://148.70.97.159:8080/vue/index.html
            socket = new Socket("148.70.97.159",8080);
//            获取输入流对象
            is = socket.getInputStream();
            //获取输出流对象
            os = socket.getOutputStream();
            //发送HTTP协议的请求部分到服务器
            os.write("GET /vue/index.html HTTP/1.1\n".getBytes());
            os.write("HOST:148.70.97.159:8080\n".getBytes());
            os.write("\n".getBytes());
            System.out.println("---------------------");
            //打印到控制台
            int i = is.read();
            while(i != -1){
                System.out.print((char)i);
                i = is.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert is != null;
                is.close();
                assert os != null;
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
