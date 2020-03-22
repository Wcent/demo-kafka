package cn.cent.demo.consumer.service;

/**
 * kafka消费者服务接口定义，订阅消息
 *
 * @author Vincent
 * @version 1.0 2020/3/23
 */
public interface ConsumerService {

    /**
     * 订阅消息，接收处理
     */
    void receiveMsg();
}
