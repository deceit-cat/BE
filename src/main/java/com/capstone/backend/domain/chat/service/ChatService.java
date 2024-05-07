package com.capstone.backend.domain.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class ChatService {

    /* AI 문제 소지의 발언 검출 */
    public boolean checkMessage(@Payload String message) {
        String baseUrl = "http://13.124.97.155:8888/";
//        String baseUrl = "http://localhost:8888/";
        String requestUrl = baseUrl + message;

        try {
            // 비동기 방식으로 HTTP 요청을 수행
            WebClient webClient = WebClient.create();
            Mono<ResponseEntity<Map>> response = webClient.get().uri(requestUrl).retrieve().toEntity(Map.class);

            // HTTP 요청의 응답을 기다려 동기적으로 처리
            ResponseEntity<Map> responseEntity = response.block();

            // 응답에서 바디 추출
            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Integer modelResult = (Integer) responseBody.get("model_result");
                //int modelResult = (int) modelResultList.get(0); // 모델 결과 확인

//                int modelResult = (int) responseBody.get("model_result");

                // 응답이 1인지 여부 반환
                return modelResult == 1;
            } else {
                // API 응답이 비어있는 경우 처리 로직 추가
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
