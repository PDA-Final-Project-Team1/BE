//package com.team1.etpipeline;
//
//import com.team1.etpipeline.kafka.service.KafkaProducerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//// 장이 닫혔을 때 테스트용 실행파일
//@SpringBootApplication
//public class ManualH0STASP0App implements CommandLineRunner {
//
//    @Autowired
//    private KafkaProducerService kafkaProducerService;
//
//    public static void main(String[] args) {
//        SpringApplication.run(ManualH0STASP0App.class, args);
//    }
//
//    @Override
//    public void run(String... args) {
//        // WebSocket에서 받았을 법한 예시 메시지
//        String mockMessage =
//                "0|H0STASP0|001|323410^150645^0^54100^54200^54300^54400^54500^54600^54700^54800^54900^55000^54000^53900^53800^53700^53600^53500^53400^53300^53200^53100^244000^159098^89337^82379^155217^142495^118070^93637^104359^136353^143210^605207^315007^279160^220093^287015^180862^106570^188467^210277^1324945^2535868^0^0^0^0^358240^-54000^5^-100.00^9805013^0^100^0^0^0";
//
//        // onMessage(...) 로직을 흉내내어 가짜 메시지를 Kafka로 전송
//        if (mockMessage.length() > 21 && mockMessage.charAt(21) == '^') {
//            String stockCode = mockMessage.substring(15, 21);
//            String datas = mockMessage.substring(22);
//
//            kafkaProducerService.sendMessage("H0STASP0", stockCode, datas);
//            System.out.println("[ManualH0STASP0App] 메시지 발행 완료 - 종목코드=" + stockCode);
//        } else {
//            System.out.println("[ManualH0STASP0App] 메시지 형식이 맞지 않습니다.");
//        }
//    }
//}
