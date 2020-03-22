package cn.cent.demo.producer.service.impl;

import cn.cent.demo.producer.dto.MsgDto;
import cn.cent.demo.producer.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Random;

/**
 * kafka生产者服务，发布消息
 *
 * @author Vincent
 * @version 1.0 2020/3/21
 */
@Service
@Slf4j
public class ProducerServiceImpl implements ProducerService {

    private static final String TOPIC = "MyTopic";
    private static final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private KafkaTemplate<String, MsgDto> kafkaTemplate;

    @Override
    public void sendMsg() {
        log.debug("begin to send message");
        MsgDto msg = new MsgDto(String.format("msg: %d", random.nextInt()));
        kafkaTemplate.send(TOPIC, msg).addCallback(
                new ListenableFutureCallback<SendResult<String, MsgDto>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("消息发布失败, topic={}, msg={}", TOPIC, msg, throwable);
                    }

                    @Override
                    public void onSuccess(SendResult<String, MsgDto> sendResult) {
                        log.debug("消息发布成功，topic={}, msg={}", TOPIC, msg);
                    }
                }
        );
        log.debug("end to send message");
    }
}
