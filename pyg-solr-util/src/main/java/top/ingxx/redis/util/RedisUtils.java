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
   //   redisTemplate.delete("seckillGoods");
//        System.out.println(redisTemplate.boundHashOps("seckillGoods").values());
//        Map<String ,Map<String , TbSeckillGoods>> dayMap = (Map) redisTemplate.boundHashOps("seckillGoods").get("2020-05-11");
//        System.out.println(dayMap.size());
//        Map<String, TbSeckillGoods> stringTbSeckillGoodsMap = dayMap.get("11:00");
//        System.out.println(stringTbSeckillGoodsMap.size());
//        System.out.println(stringTbSeckillGoodsMap.get("6").getTitle());
        //redisTemplate.delete("payLog");
        //System.out.println(redisTemplate.boundHashOps("orderList").values());
        System.out.println(redisTemplate.hasKey("seckillGoods"));

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        RedisUtils redis = (RedisUtils) context.getBean("redisUtils");
        redis.clearRedis();


    }
//public static void main(String[] args) {
//    //连接本地的 Redis 服务
//    Jedis jedis = new Jedis("localhost");
//    System.out.println("连接成功");
//    //设置 redis 字符串数据
//    jedis.set("seckillGoods", "www.runoob.com");
//    // 获取存储的数据并输出
//    System.out.println("redis 存储的字符串为: "+ jedis.get("seckillGoods"));
//}
}
