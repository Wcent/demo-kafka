package cn.cent.demo.producer.service;

/**
 * kafka生产者服务接口定义，发布消息
 *
 * @author Vincent
 * @version 1.0 2020/3/21
 */
public interface ProducerService {

    /**
     * 发布消息，发送处理
     */
    void sendMsg();
}
