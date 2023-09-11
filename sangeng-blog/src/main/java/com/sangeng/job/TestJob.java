package com.sangeng.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/11 10:44
 **/
//@Component
public class TestJob {

    @Scheduled(cron = "0/5 * * * * ?")
    public void testJob(){
        System.out.println("定时任务");
    }
}
