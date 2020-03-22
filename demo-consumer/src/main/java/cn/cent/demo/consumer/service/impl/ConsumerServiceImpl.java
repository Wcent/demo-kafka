package cn.cent.demo.consumer.service.impl;

import cn.cent.demo.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * kafka生产者服务，订阅消息
 *
 * @author Vincent
 * @version 1.0 2020/3/23
 */
@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Override
    public void receiveMsg() {
        log.debug("begin to receive message");
        log.debug("end to receive message");
    }
}
