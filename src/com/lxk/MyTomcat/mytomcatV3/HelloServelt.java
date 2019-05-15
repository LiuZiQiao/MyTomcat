package com.lxk.MyTomcat.mytomcatV3;

import java.io.IOException;

public class HelloServelt extends MyServlet {
    @Override
    public void init(MyRequest myRequest, MyResponse myResponse) {

    }

    @Override
    public void doGet(MyRequest myrequest, MyResponse myResponse) {
        try {
            myResponse.write("hello tomcat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(MyRequest myrequest, MyResponse myResponse) {
        try {
            myResponse.write("heheh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destory() {

    }
}
