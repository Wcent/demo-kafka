package cn.cent.demo.producer;

import cn.cent.demo.producer.dto.MsgDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest
@EmbeddedKafka(count = 2, ports = {9092, 9093}, partitions = 2, topics = {"MyTopic", "TopicNameTest"})
//@EmbeddedKafka(count = 1, ports = {9092}, partitions = 1, topics = {"TopicName"},
//		bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@Slf4j
class DemoProducerApplicationTests {

	private static final String GROUP_TEST = "GroupTest";
	private static final String TOPIC_NAME_TEST = "TopicNameTest";
	private static final String MY_GROUP = "MyGroup";
	private static final String MY_TOPIC = "MyTopic";

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	private Producer<String, MsgDto> initProducer() {
		Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<String, MsgDto>(producerProps).createProducer();
	}

	private Consumer<String, MsgDto> initConsumer(String group, String topic) {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(group,
				String.valueOf(true), embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "cn.cent.demo.producer.dto");
		Consumer<String, MsgDto> consumer = new DefaultKafkaConsumerFactory<String, MsgDto>(consumerProps).createConsumer();
		consumer.subscribe(Collections.singleton(topic));
		return consumer;
	}

	@Test
	void serializeAndDeserialize() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
//			// 配置Jackson序列化/反序列化日期时间格式
//			// 可解决报错：cannot deserialize from Object value (no delegate- or property-based Creator)
//			JavaTimeModule javaTimeModule = new JavaTimeModule();
//			javaTimeModule.addSerializer(LocalDateTime.class,
//					new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//			javaTimeModule.addSerializer(LocalDate.class,
//					new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//			javaTimeModule.addSerializer(LocalTime.class,
//					new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//			javaTimeModule.addDeserializer(LocalDateTime.class,
//					new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//			javaTimeModule.addDeserializer(LocalDate.class,
//					new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//			javaTimeModule.addDeserializer(LocalTime.class,
//					new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//			objectMapper.registerModule(javaTimeModule);

			String jsonString = objectMapper.writeValueAsString(new MsgDto("json string"));
			log.debug("序列化成功：{}", jsonString);

			MsgDto jsonMsg = objectMapper.readValue(jsonString, MsgDto.class);
			log.debug("反序列化成功：{}", jsonMsg);

		} catch (JsonProcessingException e) {
			Assertions.fail("MstDto序列化/序列化异常", e);
		}
	}

	@Test
	void sendAndReceive() {
		Producer<String, MsgDto> producer = initProducer();
		Consumer<String, MsgDto> consumer = initConsumer(GROUP_TEST, TOPIC_NAME_TEST);
		String payload = "Hello World";

		Future<RecordMetadata> future = producer.send(new ProducerRecord<String, MsgDto>(TOPIC_NAME_TEST, new MsgDto(payload)));
		try {
			RecordMetadata recordMetadata = future.get(1, TimeUnit.MINUTES);
			log.debug("发布消息成功：{}", recordMetadata);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.debug("获取发布消息结果超时", e);
		}

		ConsumerRecord<String, MsgDto> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME_TEST);
		log.debug("订阅消息成功：{}", consumerRecord);

		Assertions.assertEquals(payload, consumerRecord.value().getPayload());

		producer.close();
		consumer.close();
	}

	@Test
	void sendMsg() {
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e) {
			log.error("暂停中断", e);
		}

		Consumer<String, MsgDto> consumer = initConsumer(MY_GROUP, MY_TOPIC);
		ConsumerRecords<String, MsgDto> consumerRecords = KafkaTestUtils.getRecords(consumer, 1000);
		log.debug("获取{}: {}条消息", MY_TOPIC, consumerRecords.count());
		consumerRecords.forEach(record -> log.debug("订阅消息：{}", record));
		consumer.close();
	}

}
