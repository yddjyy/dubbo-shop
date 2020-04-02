package top.ingxx.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate redisTemplate;

    public void clearRedis(){
//       redisTemplate.delete("seckillGoods");
//        System.out.println(redisTemplate.boundHashOps("seckillGoods").values());
        System.out.println(redisTemplate.boundHashOps("payLog").values());
        //redisTemplate.delete("orderList");
        //System.out.println(redisTemplate.boundHashOps("orderList").values());
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        RedisUtils redis = (RedisUtils) context.getBean("redisUtils");
        redis.clearRedis();


    }
}
