package cn.cent.demo.consumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * kafka消息传输实体类
 *
 * @author Vincent
 * @version 1.0 2020/3/22
 */
@Data
@NoArgsConstructor
public class MsgDto {

    /**
     * 有效负载，消息内容
     */
    private String payload;

    /**
     * 序列化/反序列化注解可解决Jackson报错：cannot deserialize from Object value (no delegate- or property-based Creator)
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;

    /**
     * 根据数据内容构造消息对象
     * 需要注意，自定义了有参构造方法，Jackson反序列化该类对象要求定义无参构造方法
     * 否则会报错：cannot deserialize from Object value (no delegate- or property-based Creator)
     * @param payload 有效负载（数据内容）
     */
    public MsgDto(String payload) {
        this.payload = payload;
        localDateTime = LocalDateTime.now();
    }
}
