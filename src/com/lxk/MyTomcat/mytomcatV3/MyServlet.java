package com.lxk.MyTomcat.mytomcatV3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class MyServlet {
    public abstract void init(MyRequest myRequest,MyResponse myResponse);
    public abstract void doGet(MyRequest myrequest, MyResponse myResponse);
    public abstract void doPost(MyRequest myrequest, MyResponse myResponse);
    public  void Service(MyRequest myrequest, MyResponse myResponse){
        if(myrequest.getMethod().equals("doGet")){
            doGet(myrequest,myResponse);
        }else if (myrequest.getMethod().equals("doPost")){
            doPost(myrequest,myResponse);
        }
    }
    public abstract void destory();
}