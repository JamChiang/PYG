package freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import javax.rmi.CORBA.Util;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/2 3:40 PM
 * @desc todo
 */

public class FreeMarkerTest {

    @Test
    public void test() throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());

        configuration.setDefaultEncoding("utf-8");

        configuration.setClassForTemplateLoading(FreeMarkerTest.class, "/ftl");

        Template template = configuration.getTemplate("test.ftl");

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("name", "星星之火");
        dataMap.put("message", "可以燎原");
        dataMap.put("today", new Date());
        dataMap.put("number", 123456789L);

        FileWriter fileWriter = new FileWriter("/Users/apple/Desktop/test/test.html");

        template.process(dataMap, fileWriter);

        fileWriter.close();
    }

    @Test
    public void test1() {
        for (int i = 0; i <= 100; i++) {
            String num = (long) (((Math.random() * 9) + 1) * 100000) + "";
//            String num = (long) (Math.random() * 1000000) + "";
            System.out.println(num);
        }
    }

    /**
     * abc****e**f*g
     */
    @Test
    public void test2() {

        String str = "a*c**b***e**g*s";
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '*') {
                s = c + s;
            } else {
                s = s + c;
            }
        }
        System.out.println(s);
    }

}
