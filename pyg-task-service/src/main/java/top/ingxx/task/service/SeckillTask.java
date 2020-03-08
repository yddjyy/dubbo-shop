package top.ingxx.task.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {

   @Scheduled(cron = "* * * * * ?")
    public void  refreshSeckillGoods(){
       System.out.println("谁走"+new Date());
    }
}
