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
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private  static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private final EmitterRepository emitterRepository;

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param userId - 메세지를 전송할 사용자의 아이디.
     * @param eventData  - 전송할 이벤트 객체.
     */
    public void notify(Long userId, Map<String, Object> eventData) {
        sendToClient(userId, eventData);
    }

    public void startSSE(Teacher teacher) {
        Long teacherUserId = teacher.getUser().getId();
        subscribe(teacherUserId);
    }

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param userId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "Welcome! New EventStream Created."); // 더미 데이터 전송
        sendSubscriptionRequest(userId); // emitter 구독
        return emitter;
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param userId - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        return emitter;
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param userId   - 데이터를 받을 사용자의 아이디.
     * @param eventData - 전송할 데이터.
     */

    private void sendToClient(Long userId, Object eventData) { // 이벤트 전송
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(eventData));
            } catch (IOException exception) {
                emitterRepository.deleteById(userId);
                emitter.completeWithError(exception);
            }
        }
    }

    /**
     * SSE 구독 요청 보냄
     * @param teacherUserId
     */
    private void sendSubscriptionRequest(Long teacherUserId) {
        String url = "http://13.124.97.155:8080/notify/subscribe/" + teacherUserId;
        logger.info("URL로 Get 요청을 보내기 직전 \"{}\"", url);

        // GET 요청 보내기
        WebClient client = WebClient.create();
        client.get()
            .uri(url)
            .retrieve()
            .toBodilessEntity()
            .subscribe(
                response -> {
                    logger.info("URL로 Get 요청을 보냄 : {} ", url);
                    logger.info("서버에서의 응답: {}", response.getStatusCode());
                },
                error -> logger.error("Error sending subscription request to URL: {}", url, error)
            );
    }

    public void followRequest(User parentUser, User teacherUser) {
        SseEmitter parentEmitter = emitterRepository.get(parentUser.getId());
        SseEmitter teacherEmitter = emitterRepository.get(teacherUser.getId());

        if (parentEmitter != null && teacherEmitter != null) {
            try {
                parentEmitter.send("친구 추가 요청: " + teacherUser.getName());
                teacherEmitter.send("친구 요청 도착: " + parentUser.getName());
            } catch (IOException e) {
                System.err.println("알림 전송 중 오류 발생 " + e.getMessage());
            }
        }
    }

    public void removeEmitter(Long userId) {
        emitterRepository.deleteById(userId);
    }

}