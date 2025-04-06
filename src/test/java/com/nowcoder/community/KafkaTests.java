package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;
    @Test
    public void testKafka() throws InterruptedException {
            kafkaProducer.sentMessage("test", "hello");
            kafkaProducer.sentMessage("test", "world");
            try{
                Thread.sleep(1000 * 10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
    }

}

@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sentMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

@Component
class KafkaConsumer {

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord msg) {
        System.out.println(msg.value());
    }


}
