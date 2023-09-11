package com.sangeng.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/11 10:41
 **/
//@Component
public class TestRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("init...");
    }
}
