package com.lxk.MyTomcat.mytomcatV2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class TestServer {
    public static String WEB_ROOT = System.getProperty("user.dir")+"\\"+"WebContent";
    private static String url="";

    //存放服务端conf.properties 中的配置信息
    private static Map<String,String> map = new HashMap<>();

    static{
        //服务器启动之前将配置信息加载到map中
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(WEB_ROOT+"\\conf.properties"));
            Set set = prop.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()){
                String key = (String) iterator.next();
                String value = prop.getProperty(key);
                map.put(key,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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

                //判断请求的是静态还是运行在服务端的java程序
                if(null != url){
                    if(url.indexOf(".")!=-1){
                        //发送静态资源
                        sendStaticResource(os);
                    }else {
                        sendDynamicResource(is,os);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sendDynamicResource(InputStream is, OutputStream os) throws Exception {
        //发送HTTP响应头
        os.write("HTTP/1.1 200 OK\n".getBytes());
        os.write("Server:apach-Coyote/1.1\n".getBytes());
        os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
        os.write("\n".getBytes());
        //判断map中的key是否存在一个key是否和本次请求的资源路径一致
        if (map.containsKey(url)){
            //如果包含key，获取value
            String value = map.get(url);
            Class clazz = Class.forName(value);
            //通过反射对应的java程序加载到内存
            //执行init方法
            //执行service方法
            Servlet servlet = (Servlet) clazz.newInstance();
            servlet.init();
            servlet.service(is,os);
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

