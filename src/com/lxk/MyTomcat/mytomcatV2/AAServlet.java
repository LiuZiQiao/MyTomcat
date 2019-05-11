package com.lxk.MyTomcat.mytomcatV2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AAServlet implements Servlet {
    @Override
    public void init() {
        System.out.println("Ainit-----");
    }

    @Override
    public void service(InputStream is, OutputStream os) throws IOException {
        System.out.println("Aservice-----");
        os.write("aaa".getBytes());
        os.flush();
    }
    @Override
    public void destory() {
        System.out.println("ADestory-----");

    }
}
