package com.capstone.backend.domain.notification.service;

import com.capstone.backend.domain.notification.repository.EmitterRepository;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private  static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final WebClient.Builder webClientBuilder;

    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param userId - 메세지를 전송할 사용자의 아이디.
     * @param event  - 전송할 이벤트 객체.
     */
    public void notify(Long userId, Object event) {
        sendToClient(userId, event);
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param id   - 데이터를 받을 사용자의 아이디.
     * @param data - 전송할 데이터.
     */
    private void sendToClient(Long id, Object data) {
        SseEmitter emitter = emitterRepository.get(id);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data(data));
            } catch (IOException exception) {
                emitterRepository.deleteById(id);
                emitter.completeWithError(exception);
            }
        }
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param id - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }

    public void followRequest(User parentUser, User teacherUser) {
        SseEmitter parentEmitter = emitterRepository.get(parentUser.getId());
        SseEmitter teacherEmitter = emitterRepository.get(teacherUser.getId());

        if (parentEmitter != null && teacherEmitter != null) {
            try {
                parentEmitter.send("친구 추가 요청: " + teacherUser.getName());
                teacherEmitter.send("친구 요청 도착: " + parentUser.getName());
            } catch (IOException e) {
                // 예외 처리
            }
        }
    }

    public void startSSESubscriptionForTeacher(Teacher teacher) {
        Long teacherUserId = teacher.getUser().getId();
        SseEmitter emitter = subscribe(teacherUserId);
        sendSubscriptionRequest(emitter, teacherUserId);
    }

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param userId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");
//        sendSubscriptionRequest(emitter, userId);
        return emitter;
    }

    private void sendSubscriptionRequest(SseEmitter emitter, Long teacherUserId) {
        String url = "http://localhost:8080/notify/subscribe/" + teacherUserId;
        logger.info("SENDING GET URL : {}", url);

//        // GET 요청 보내기
//        WebClient client = WebClient.create();
//        String response = client.get()
//                .uri(url)
//                .retrieve()
//                .bodyToMono(String.class)
////                .subscribe(
////                    response -> {
////                        logger.info("Subscription request sent to URL : {} ", url);
////                        logger.info("Response from server: {}", response);
////                    },
////                    error -> logger.error("Error sending subscription request to URL: {}", url, error)
////                );
////                .block();
//
//                logger.info("Response from server: {}", response);
//                logger.info("Subscription request sent to URL : {}", url);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            // 응답 처리 로직
            logger.info("Response from server: {}", response);
            logger.info("Subscription request sent to URL : {}", url);
        } catch (Exception e) {
            // 예외 처리 로직
            logger.error("Error sending subscription request to URL: {}", url, e);
            emitter.completeWithError(e);
        }
    }

    public void removeEmitter(Long userId) {
        emitterRepository.deleteById(userId);
    }

}