package com.test.redis;

import com.test.redis.pojo.UserEntityTest;
import com.test.redis.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author by Lixq
 * @Classname RedisTest
 * @Description TODO
 * @Date 2021/5/17 22:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> serializableRedisTemplate;
    @Autowired
    private RedisService redisService;

    @Test
    public void testString() {
        strRedisTemplate.opsForValue().set("strKey", "zzzz");
        System.out.println(strRedisTemplate.opsForValue().get("strKey"));
    }

    @Test
    public void testSerializable() {
        UserEntityTest user=new UserEntityTest();
        user.setId(1L);
        user.setUserName("朝雾轻寒");
        user.setUserSex("男");
        serializableRedisTemplate.opsForValue().set("user", user);
        UserEntityTest user2 = (UserEntityTest) serializableRedisTemplate.opsForValue().get("user");
        System.out.println("user:"+user2.getId()+","+user2.getUserName()+","+user2.getUserSex());
    }

    @Test
    public void testExpire() {
        redisService.set("testExpire","测试1分钟失效",60);
        System.out.println(redisService.get("testExpire"));
    }
}
