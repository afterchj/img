package com.tpadsz.img;

import com.tpadsz.img.api.ImageManager;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hongjian.chen on 2017/3/31.
 */
public class MyClient {

    @Test
    public void test() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:conf/consumer.xml");
        ImageManager imageManager = (ImageManager) ctx.getBean("imageManager");
        String value = imageManager.show("code", "12312", "mobile", "18170756879");
        System.out.println("value=" + value);
    }
}


