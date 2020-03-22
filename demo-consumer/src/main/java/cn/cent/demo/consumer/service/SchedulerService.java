package cn.cent.demo.consumer.service;

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
    private ConsumerService consumerService;

    /**
     * 服务启后10秒开始跑间隔1秒定时任务，订阅消息
     */
    @Scheduled(initialDelay = 10*1000, fixedRate = 1000)
    private void doJob() {
        log.debug("跑定时任务");
        consumerService.receiveMsg();
    }
}
