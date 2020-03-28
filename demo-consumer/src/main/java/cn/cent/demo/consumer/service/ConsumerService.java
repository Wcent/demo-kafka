package cn.cent.demo.consumer.service;

import cn.cent.demo.consumer.dto.MsgDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

/**
 * kafka消费者服务接口定义，订阅消息
 *
 * @author Vincent
 * @version 1.0 2020/3/23
 */
public interface ConsumerService {

    /**
     * 订阅消息，接收处理
     * @param record 消费记录
     */
    void receiveMsg(ConsumerRecord<String, MsgDto> record);

    /**
     * 订阅消息，接收处理，手工提交偏移量
     * @param record 消费记录
     * @param consumer 消费组
     */
    void rtvMsgCommit(ConsumerRecord<String, MsgDto> record, Consumer<String, MsgDto> consumer);

    /**
     * 订阅消息，接收处理，手工消费提交偏移量反馈
     * Acknowledgment配合手工消费提交偏移量使用，需要配置以下属性
     * spring.kafka.consumer.enable-auto-commit=false
     * spring.kafka.listener.ack-mode=manual
     * @param record 消费记录
     * @param acknowledgment 反馈
     */
//    void rtvMsgAck(ConsumerRecord<String, MsgDto> record, Acknowledgment acknowledgment);
}
