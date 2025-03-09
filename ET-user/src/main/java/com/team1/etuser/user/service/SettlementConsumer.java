package com.team1.etuser.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.dto.SettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementConsumer {

    private final SettlementService settlementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "settlement", groupId = "etuser-settlement-group")
    public void consumeTradeMessage(ConsumerRecord<String, String> record) throws JsonProcessingException {
        SettlementDTO settlementDTO = objectMapper.readValue(record.value(), SettlementDTO.class);

        settlementService.processSettlement(settlementDTO);
    }
}