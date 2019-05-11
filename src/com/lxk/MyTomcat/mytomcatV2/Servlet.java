package com.lxk.MyTomcat.mytomcatV2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Servlet {
    public void init();
    public void service(InputStream is, OutputStream os) throws IOException;
    public void destory();
}
