package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.tools.java.ClassPath;

import java.util.List;
import java.util.Set;

/**
 * @author JW
 * @createTime 2018/10/30 3:38 PM
 * @desc todo
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class redisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //测试字符串 objing
    @Test
    public void testString() {
        redisTemplate.boundValueOps("string_key").set("传智播客");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    //测试散列 hash
    @Test
    public void testHash() {
        redisTemplate.boundHashOps("hash_key").put("key_1","v_1");
        redisTemplate.boundHashOps("hash_key").put("key_2","v_2");

        List list = redisTemplate.boundHashOps("hash_key").values();

        System.out.println(list);
    }

    //测试列表 list
    @Test
    public void testList() {
        redisTemplate.boundListOps("list_key").rightPush("c");
        redisTemplate.boundListOps("list_key").leftPush("b");
        redisTemplate.boundListOps("list_key").leftPush("a");
        redisTemplate.boundListOps("list_key").rightPush("d");

        List list = redisTemplate.boundListOps("list_key").range(0, -1);

        System.out.println(list);
    }

    //测试集合 set
    @Test
    public void testSet() {
        redisTemplate.boundSetOps("set_key").add("a","1",123,"itecast",12);
        Set set = redisTemplate.boundSetOps("set_key").members();

        System.out.println(set);
    }

    //测试有序集合 zset
    @Test
    public void testZset() {
        redisTemplate.boundZSetOps("zset_key").add("a", 20);
        redisTemplate.boundZSetOps("zset_key").add("b", 5);
        redisTemplate.boundZSetOps("zset_key").add("c", 10);

        Set zset = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println(zset);
    }

}
