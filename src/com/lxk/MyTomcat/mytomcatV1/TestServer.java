package com.lxk.MyTomcat.mytomcatV1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static String WEB_ROOT = System.getProperty("user.dir")+"\\"+"WebContent";
    private static String url="";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try{
            serverSocket = new ServerSocket(8080);
            while (true){
                socket = serverSocket.accept();
                is = socket.getInputStream();
                os = socket.getOutputStream();
                parseHandler(is);
                sendStaticResource(os);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void parseHandler(InputStream is) throws IOException {
        StringBuffer content = new StringBuffer();
        //存放HTTP协议的请求头部
        byte[] buf = new byte[1024];
        int i=-1;
        i = is.read(buf);
        for (int j=0;j<i;j++){
            content.append((char)buf[j]);
        }
        //截取请求的资源路径，赋值给url
        parseUrl(content.toString());
        //控制台打印查看
        System.out.println(content);
    }

    private static void parseUrl(String s) {
        int index1,index2 = 0;
        index1 = s.indexOf(" ");
        if (index1 != -1){
            index2 = s.indexOf(" ",index1+1);
        }
        if(index2 > index1){
            url = s.substring(index1+2,index2);
        }
        System.out.println(url);
    }
    //发送资源
    private static void sendStaticResource(OutputStream os) throws IOException {
        //获取资源内容
        byte[] bytes = new byte[1024];
        FileInputStream fis = null;
        try{
            File file = new File(WEB_ROOT,url);

            if (file.exists()){
                //向客户端输出HTTP协议头
                os.write("HTTP/1.1 200 OK\n".getBytes());
                os.write("Server:apach-Coyote/1.1\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                os.write("\n".getBytes());
                //获取文件输入流对象
                fis = new FileInputStream(file);
                int ch = fis.read(bytes);
                while (ch != -1){
                    os.write(bytes,0,ch);
                    ch = fis.read(bytes);
                }
            }else{
                //资源不存在
                os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
                os.write("Server:apach-Coyote/1.1\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                os.write("\n".getBytes());
                String errorMessage = url+" not found";
                os.write(errorMessage.getBytes());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fis != null){
                fis.close();
                fis = null;
            }
        }
    }
}

