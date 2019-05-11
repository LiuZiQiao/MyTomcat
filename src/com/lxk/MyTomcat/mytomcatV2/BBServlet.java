package com.lxk.MyTomcat.mytomcatV2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BBServlet implements Servlet {
    @Override
    public void init() {
        System.out.println("Binit-----");
    }

    @Override
    public void service(InputStream is, OutputStream os) throws IOException {
        System.out.println("Bservice-----");
        os.write("bbb".getBytes());
        os.flush();
    }
    @Override
    public void destory() {
        System.out.println("BDestory-----");

    }
}
