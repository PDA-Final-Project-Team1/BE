package com.team1.etuser.stock.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etuser.stock.service.SettlementService;
import com.team1.etuser.user.dto.SettlementRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementConsumer {

    private final SettlementService settlementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "settlement", groupId = "etuser-settlement-group")
    public void consumeTradeMessage(ConsumerRecord<String, String> record) throws JsonProcessingException {
        SettlementRes settlementRes = objectMapper.readValue(record.value(), SettlementRes.class);

        settlementService.processSettlement(settlementRes);
    }
}