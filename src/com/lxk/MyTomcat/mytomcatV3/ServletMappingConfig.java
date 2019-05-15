package com.lxk.MyTomcat.mytomcatV3;

import java.util.ArrayList;
import java.util.List;


//当然一般这个配置是通过xml文件去配置的。
public class ServletMappingConfig {
    public static List<ServletMapping>  servletMappingList = new ArrayList<>();

    static {
            servletMappingList.add(new ServletMapping("HelloWorld","/world","mytomcat.HelloWorldServelt"));
            }
}
