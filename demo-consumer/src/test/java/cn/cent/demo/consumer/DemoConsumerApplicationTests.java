package cn.cent.demo.consumer;

import cn.cent.demo.consumer.dto.MsgDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest
//@EmbeddedKafka(count = 2, ports = {9092, 9093}, partitions = 4, topics = {"MyTopic", "TopicNameTest"})
@EmbeddedKafka(count = 2, ports = {9092, 9093}, partitions = 2, topics = {"MyTopic", "TopicNameTest"},
		bootstrapServersProperty = "spring.kafka.bootstrap-servers")
//@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@Slf4j
class DemoConsumerApplicationTests {

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
		consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "cn.cent.demo.consumer.dto");
		Consumer<String, MsgDto> consumer = new DefaultKafkaConsumerFactory<String, MsgDto>(consumerProps).createConsumer();
		consumer.subscribe(Collections.singleton(topic));
		return consumer;
	}

	@Test
	void sendAndReceive() {
		Producer<String, MsgDto> producer = initProducer();
		Consumer<String, MsgDto> consumer = initConsumer(MY_GROUP, TOPIC_NAME_TEST);
		Consumer<String, MsgDto> consumerTest = initConsumer(GROUP_TEST, TOPIC_NAME_TEST);

		MsgDto msgDto = new MsgDto("Msg: Hello World!");
		Future<RecordMetadata> future = producer.send(new ProducerRecord<>(TOPIC_NAME_TEST, msgDto));
		try {
			RecordMetadata recordMetadata = future.get(1, TimeUnit.MINUTES);
			log.debug("发布消息到{}成功：{}", recordMetadata.topic(), msgDto);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error("获取发布结果异常", e);
		}

		ConsumerRecord<String, MsgDto> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME_TEST);
		log.debug("消费组{}的消费者从{}获取消息：{}", MY_GROUP, TOPIC_NAME_TEST, consumerRecord);

		ConsumerRecord<String, MsgDto> record = KafkaTestUtils.getSingleRecord(consumerTest, TOPIC_NAME_TEST);
		log.debug("消费组{}的消费组从{}获取消息：{}", GROUP_TEST, TOPIC_NAME_TEST, record);

		producer.close();
		consumer.close();
		consumerTest.close();
	}

	@Test
	void receiveMsg() {
		Producer<String, MsgDto> producer = initProducer();
		Random random = new Random(System.currentTimeMillis());
		final int bound = 5;

		for (int i = 0; i < bound; i++) {
			MsgDto msgDto = new MsgDto("Msg: " + (random.nextInt(bound)));
			Future<RecordMetadata> future = producer.send(new ProducerRecord<>(MY_TOPIC, msgDto));
			try {
				RecordMetadata recordMetadata = future.get(1, TimeUnit.MINUTES);
				log.debug("发布消息到{}成功：{}", recordMetadata.topic(), msgDto);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("获取发布结果异常", e);
			}
		}

		// waiting for consumer
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			log.error("暂停中断", e);
		}

		producer.close();
	}


}
