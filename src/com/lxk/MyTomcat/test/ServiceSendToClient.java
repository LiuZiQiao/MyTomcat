package com.lxk.MyTomcat.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceSendToClient {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        ServerSocket serverSocket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            serverSocket = new ServerSocket(8080);
            while(true)
            {
                socket = serverSocket.accept();
                os = socket.getOutputStream();

                os.write("HTTP/1.1 200 OK\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                os.write("Server:Apache-Coyote/1.1\n".getBytes());
                os.write("\n\n".getBytes());

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("<html>");
                stringBuffer.append("<head><title>Test</title></head>");
                stringBuffer.append("<body>");
                stringBuffer.append("<h1> aaaaaaaaa</h1>");
                stringBuffer.append("<a href='http://www.baidu.com'>百度</a>");
                stringBuffer.append("</body>");
                stringBuffer.append("</html>");
                os.write(stringBuffer.toString().getBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(os != null){
                os.close();
                os = null;
            }
            if(socket != null){
                socket.close();
                socket = null;
            }
        }
    }

}
