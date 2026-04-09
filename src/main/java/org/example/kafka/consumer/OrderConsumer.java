package org.example.kafka.consumer;

import org.example.kafka.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = "${kafka.topic.orders}", groupId = "order-group")
    public void consumeOrder(
            @Payload Order order,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("📦 Received: {} | {} x{} | Partition: {} | Offset: {}",
                order.getOrderId(),
                order.getProduct(),
                order.getQuantity(),
                partition,
                offset);
    }
}