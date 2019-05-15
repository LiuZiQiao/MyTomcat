package com.lxk.MyTomcat.mytomcatV3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyTomcat {
    private int port = 8888;
    private Map<String ,String> urlSerlvetMap = new HashMap<>();

    private Selector selector;
    private ExecutorService es = Executors.newCachedThreadPool();

    public MyTomcat() {
    }

    public MyTomcat(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        //初始化映射关系
        InitServletMapping();
        //启动Selector
        selector = SelectorProvider.provider().openSelector();
        //启动Channel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //配置非阻塞选择
        ssc.configureBlocking(false);
        //监听端口
        InetSocketAddress isa = new InetSocketAddress(port);
        ssc.socket().bind(isa);

        //将Channel绑定到Slector上，并选择准备模式为Accept
        SelectionKey acceptKey = ssc.register(selector,SelectionKey.OP_ACCEPT);

        System.out.println("MyTomcat is started...");

        ConcurrentLinkedQueue<MyRequest> requestsList = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<MyResponse> responsesList = new ConcurrentLinkedQueue<>();

        while (true){
            //等待Channel准备数据
            selector.select();
            Set readyKey = selector.selectedKeys();
            Iterator iterator = readyKey.iterator();

            while (iterator.hasNext()){
                SelectionKey sk = (SelectionKey) iterator.next();
                iterator.remove();

                if(sk.isAcceptable()){
                    doAccept(sk);
                }else if(sk.isValid() && sk.isReadable()){
                    requestsList.add(getRequest(sk));
                    sk.interestOps(SelectionKey.OP_WRITE);
                }else if(sk.isValid() && sk.isWritable()){
                    responsesList.add(getResponse(sk));
                    sk.interestOps(SelectionKey.OP_READ);
                }

                //等待一对请求和响应均准备好处理
                if (!requestsList.isEmpty() &&! responsesList.isEmpty()){
                    disPatch(requestsList.poll(),responsesList.poll());
                }
            }
        }
    }
    //尝试开启接收模式
    private void doAccept(SelectionKey selectionKey){
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel clienChannel;
        try{
            clienChannel = serverSocketChannel.accept();
            clienChannel.configureBlocking(false);
            SelectionKey  clientKey = clienChannel.register(selector,SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从通道中获取请求并进行包装
    private MyRequest getRequest(SelectionKey selectionKey) throws IOException {
        return  new MyRequest(selectionKey);
    }

    private MyResponse getResponse(SelectionKey selectionKey){
        return new MyResponse(selectionKey);
    }

    private void InitServletMapping(){
        for (ServletMapping servletMapping:ServletMappingConfig.servletMappingList){
            urlSerlvetMap.put(servletMapping.getUrl(),servletMapping.getClazz());
        }
    }

    private void disPatch(MyRequest myRequest, MyResponse myResponse){
        if (myRequest == null) return;
        if (myResponse == null) return;

        String clazz = urlSerlvetMap.get(myRequest.getUrl());

        try {
            if (clazz == null){
                myResponse.write("404");
                return;
            }
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        Class<MyServlet> myServletClass = (Class<MyServlet>) Class.forName(clazz);
                        MyServlet myServlet = myServletClass.newInstance();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new MyTomcat().start();
    }
}
