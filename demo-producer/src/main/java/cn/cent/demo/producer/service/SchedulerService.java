package cn.cent.demo.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 定时调度服务
 *
 * @author Vincent
 * @version 1.0 2020/3/21
 */
@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private ProducerService producerService;

    /**
     * 服务启后5秒开始跑间隔2秒定时任务，发布消息
     */
    @Scheduled(initialDelay = 5000, fixedRate = 1000)
    private void doJob() {
        log.debug("跑定时任务");
        producerService.sendMsg();
    }
}
