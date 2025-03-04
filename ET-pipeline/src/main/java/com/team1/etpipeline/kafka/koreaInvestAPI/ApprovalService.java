package com.team1.etpipeline.kafka.koreaInvestAPI;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class ApprovalService {

    @Value("${korea-investment.app-key}")
    private String appKey;

    @Value("${korea-investment.secret-key}")
    private String secretKey;


    @Value("${korea-investment.app-key2}")
    private String appKey2;

    @Value("${korea-investment.secret-key2}")
    private String secretKey2;

    @Value("${korea-investment.base-url}")
    private String baseUrl;

    private final WebClient webClient;
    //
//    public ApprovalService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
//    }
    public ApprovalService(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5))
                .secure();  // 이렇게 변경

        this.webClient = webClientBuilder
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    public String getApprovalKey(String type) {
        Map<String, String> body;
        if(type.equals("trade")) {
            body = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appKey,
                    "secretkey", secretKey
            );
        }
        else{
            body = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appKey2,
                    "secretkey", secretKey2
            );
        }

        return webClient.post()
                .uri("/oauth2/Approval")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.get("approval_key").asText())
                .doOnSuccess(key -> log.info("Successfully obtained approval key"))
                .doOnError(error -> log.error("Error obtaining approval key: {}", error.getMessage()))
                .block();
    }

    public String decodeAES256(String key, String iv, String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decoding AES256: {}", e.getMessage());
            throw new RuntimeException("Failed to decode AES256", e);
        }
    }
}