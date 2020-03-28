package cn.cent.demo.consumer.service.impl;

import cn.cent.demo.consumer.dto.MsgDto;
import cn.cent.demo.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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
    @KafkaListener(topics = "MyTopic", groupId = "MyGroup")
    public void receiveMsg(ConsumerRecord<String, MsgDto> consumerRecord) {
        log.debug("消费组MyGroup订阅{}主题{}分区消息成功：{}",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.value());
    }

    @Override
    @KafkaListener(topics = "MyTopic", groupId = "CommitGroup")
    public void rtvMsgCommit(ConsumerRecord<String, MsgDto> record, Consumer<String, MsgDto> consumer) {
        log.debug("消费组CommitGroup订阅{}主题{}分区消息成功：{}",
                record.topic(), record.partition(), record.value());
        consumer.commitSync();
    }

    /**
     * Acknowledgment配合手工消费提交偏移量使用，需要配置以下属性
     * spring.kafka.consumer.enable-auto-commit=false
     * spring.kafka.listener.ack-mode=manual
     * @param record 消费记录
     * @param acknowledgment 消费反馈
     */
//    @KafkaListener(topics = "MyTopic", groupId = "AckGroup")
//    public void rtvMsgAck(ConsumerRecord<String, MsgDto> record, Acknowledgment acknowledgment) {
//        log.debug("消费组AckGroup订阅{}主题{}分区消息成功：{}",
//                record.topic(), record.partition(), record.value());
//        acknowledgment.acknowledge();
//    }
}
