package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.notification.service.NotificationService;
import com.capstone.backend.domain.user.dto.*;
import com.capstone.backend.domain.user.entity.*;
import com.capstone.backend.domain.user.repository.*;
import com.capstone.backend.global.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.*;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserLoginCountRepository userLoginCountRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final FriendRepository friendRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserLoginCountRepository userLoginCountRepository,
                       TeacherRepository teacherRepository,
                       ParentRepository parentRepository,
                       ChildRepository childRepository,
                       FriendRepository friendRepository,
                       ChatRoomRepository chatRoomRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       NotificationService notificationService
                       ) {
        this.userRepository = userRepository;
        this.userLoginCountRepository = userLoginCountRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.childRepository = childRepository;
        this.friendRepository = friendRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }
    private static final String USER_NOT_FOUND_MESSAGE = "해당 사용자를 찾을 수 없습니다: ";

    /** 회원가입
     *
     */
    public void signUp(UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(Role.GUEST)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    /**
     * 추가정보 입력 (이 시점에 Teacher, Parent 유저로 삽입)
     * @param userDto 기본 유저
     * @param accessToken 고유한 인증을 위함
     */
    public void addInfo(UserDto userDto, String accessToken) throws Exception {
        try {
            User user = validateAccessTokenAndGetUser(accessToken);

            if (user.getRole() == Role.GUEST) {
                if (userDto.getRole() == null) {
                    throw new IllegalArgumentException("사용자 역할 정보가 null 입니다.");
                }

                if (userDto.getRole() == Role.PARENT) { // PARENT
                    Parent parent = new Parent(user, userDto.getChildNum());

                    for (ChildDto dto : userDto.getChildren()) {
                        Child child = new Child(parent, dto); // 🧚🏻‍ teacher_id 는 친구요청 수락시 학부모 매핑과 함께 db에 저장
                        childRepository.save(child);
                    }

                    parentRepository.save(parent);

                    user.setRole(Role.PARENT);
                    userRepository.save(user);

                    /* 부모의 자식 정보와 일치하는 선생님을 찾아 친구 추가 요청 알림 보내기*/
//                    followRequest(parent);

                } else if (userDto.getRole() == Role.TEACHER) { // TEACHER
                    if (userDto.getTeacherSchool() == null) {
                        throw new IllegalArgumentException("선생님의 학교 정보를 입력하세요.");
                    }
                    if (userDto.getTeacherClass() == null) {
                        throw new IllegalArgumentException("선생님의 반 정보를 입력하세요.");
                    }

                    Teacher teacher = new Teacher(
                            user,
                            userDto.getTeacherSchool(),
                            userDto.getTeacherClass()
                    );

                    teacherRepository.save(teacher);

                    user.setRole(Role.TEACHER);
                    userRepository.save(user);

                    /* Teacher 정보 저장 후 SSE 구독 시작 */
                    startSSE(teacher);

                } else {
                    throw new Exception("이미 유저 구분이 설정되었습니다.");
                }
            } else {
                throw new Exception("해당 이메일을 가진 사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 부모의 자식 정보와 일치하는 선생님을 찾아 친구 추가 요청 알림 보내기
     * @param parent 부모 정보 가져오기
     */
//    public void followRequest(Parent parent) {
//        List<Child> children = parent.getChildren();
//
//        for (Child child : children) {
//            Teacher teacher = teacherRepository.findCompare(child.getChildSchool(), child.getChildClass());
//
//            if (teacher != null && teacher.getTeacherName().equals(child.getTeacherName())) {
//                Long teacherUserId = teacher.getUser().getId();
//                if (teacherUserId == null) {
//                    System.out.println("teacher 의 userId 가 null 입니다. teacher: " + teacher);
//                } else {
//                    System.out.println("teacher 의 userId : " + teacherUserId);
//                    // 부모와 선생님 사이의 친구 추가 요청 알림을 보냅니다.
//                    notificationService.followRequest(parent.getUser(), teacher.getUser());
//
//                    // sse 메세지 발송
//                    Map<String, Object> eventData = new HashMap<>();
//                    eventData.put("message", child + "의 학부모 " + parent.getUser().getName() + "님의 친구 추가 요청을 수락하시겠습니까?");
//                    notificationService.notify(teacherUserId, eventData);
//                }
//            }
//        }
//    }

    /**
     * SSE 구독 시작
     * @param teacher
     */
    public void startSSE(Teacher teacher) {
        notificationService.startSSE(teacher);
    }

    public User validateAccessTokenAndGetUser(String accessToken) throws Exception {
        if (jwtService.isTokenValid(accessToken)) {
            Optional<String> extractedEmail = jwtService.extractEmail(accessToken);
            if (extractedEmail.isPresent()) {
                // 여기서 userRepository.findByEmail()을 통해 사용자를 찾고 반환합니다.
                return userRepository.findByEmail(extractedEmail.get())
                        .orElseThrow(() -> new Exception("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
            } else {
                throw new Exception("토큰에서 이메일을 추출할 수 없습니다.");
            }
        } else {
            throw new Exception("유효하지 않은 토큰입니다.");
        }
    }

    /**
     * 부모 토큰으로 아이들의 선생님 찾기
     *
     */
    public List<TeacherDto> findTeachers(String accessToken) throws Exception {
        try {
            User user = validateAccessTokenAndGetUser(accessToken);
            if (user.getRole() != Role.PARENT) {
                throw new IllegalStateException("부모로 설정된 사용자가 아닙니다.");
            }

            Optional<Parent> parentOptional = parentRepository.findByUser(user);

            if (parentOptional.isPresent()) {
                Parent parent = parentOptional.get();
                List<TeacherDto> teachers = new ArrayList<>();
                for (Child child : parent.getChildren()) {
                    List<Teacher> foundTeachers = teacherRepository.findByTeacherSchoolAndTeacherClassAndUser_Name(
                            child.getChildSchool(),
                            child.getChildClass(),
                            child.getTeacherName()
                    );
                    for (Teacher teacher : foundTeachers) {
                        TeacherDto teacherDto = new TeacherDto();
                        teacherDto.setTeacherId(teacher.getUser().getId());
                        teacherDto.setTeacherName(teacher.getTeacherName());
                        teacherDto.setTeacherSchool(teacher.getTeacherSchool());
                        teacherDto.setTeacherClass(teacher.getTeacherClass());
                        teacherDto.setChildName(child.getChildName());
                        teachers.add(teacherDto);
                    }
                }
                return teachers;
            } else {
                throw new IllegalStateException("부모가 존재하지 않습니다. UserServicve");
            }

        } catch (Exception e) {
            throw new Exception("사용자 엑세스 토큰을 검증하는 도중 오류가 발생했습니다.", e);
        }
    }

    public Teacher findTeacherById(Long teacherUserId) {
        Optional<Teacher> teacherOptional = teacherRepository.findByUserId(teacherUserId);
        return teacherOptional.orElse(null);
    }

    public Parent findParentById(Long parentUserId) {
        Optional<Parent> parentOptional = parentRepository.findByUserId(parentUserId);
        return parentOptional.orElse(null);
    }

    public User findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }

    public Map<String, Object> loginUser(String email, String password) {

        User user = validateUserExistsByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀립니다.");
        }

        updateLoginCount(user);
        return generateTokens(user);
    }


    private Map<String, Object> generateTokens(User user) {
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", jwtService.createAccessToken(user.getEmail(), user.getName(), user.getRole()));
        tokens.put("refreshToken", jwtService.createRefreshToken());
        return tokens;
    }

    public void updateLoginCount(User user) {
        LocalDate today = getToday();
        int weekOfYear = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = today.getYear();

        UserLoginCount userLoginCount = userLoginCountRepository
                .findByUserAndLoginYearAndLoginWeek(user, year, weekOfYear)
                .orElseGet(() -> createUserLoginCount(user, today, year, weekOfYear));

        userLoginCount.incrementCount();
        userLoginCountRepository.save(userLoginCount);
    }

    private UserLoginCount createUserLoginCount(User user, LocalDate loginDate, int loginYear, int loginWeek) {
        return UserLoginCount.builder()
                .user(user)
                .loginDate(loginDate)
                .loginYear(loginYear)
                .loginWeek(loginWeek)
                .build();
    }

//    // 사용자별 주간 로그인 횟수 조회
//    public long getUserWeeklyLoginCount(long userId) {
//        LocalDate today = getToday();
//        isValidUser(userId);
//        return userLoginCountRepository.sumLoginCountsByUserAndYearAndWeek(userId, today.getYear(), today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))
//                .orElse(0);
//    }

    // 전체 사용자의 주간 로그인 횟수 조회
    public long getTotalWeeklyLoginCount() {
        LocalDate today = getToday();
        return userLoginCountRepository.sumTotalLoginCountsByYearAndWeek(today.getYear(), today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))
                .orElse(0);
    }

    private void isValidUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException((USER_NOT_FOUND_MESSAGE + userId)));
    }

    private User validateUserExistsByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException((USER_NOT_FOUND_MESSAGE + email)));
    }

    private LocalDate getToday() {
        return LocalDate.now();
    }

    public Role getUserRole(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getRole();
        } else {
            throw new IllegalArgumentException("이메일에 해당하는 사용자가 없습니다.");
        }
    }

    public Long getUserId(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getId();
        } else {
            throw new RuntimeException("해당하는 이메일을 가진 사용자를 찾을 수 없습니다.");
        }
    }

    // 전체 유저 수 조회
    public Long getUserCount() {
        return userRepository.count();
    }

    public void setWorkStatus(Long userId, TeacherStatusDto teacherStatusDto) {
        Teacher teacher = findTeacherById(userId);
        if (teacher == null) {
            throw new NoSuchElementException("해당 선생님을 찾을 수 없습니다.");
        }

        teacher.setDuty(teacherStatusDto.isDuty());
        if (teacherStatusDto.isDuty()) {
            teacher.setWorkStart(teacherStatusDto.getWorkStart());
            teacher.setWorkEnd(teacherStatusDto.getWorkEnd());
        } else {
            teacher.setDisturbStart(teacherStatusDto.getDisturbStart());
            teacher.setDisturbEnd(teacherStatusDto.getDisturbEnd());
        }

        teacherRepository.save(teacher);
    }

    public TeacherStatusDto getWorkStatus(Long teacherUserId) {
//        Teacher teacher = findTeacherById(userId);
//        if (teacher == null) {
//            throw new NoSuchElementException("해당 선생님을 찾을 수 없습니다.");
//        }
//
//        TeacherStatusDto teacherStatusDto = new TeacherStatusDto();
//        teacherStatusDto.setDuty(teacher.isDuty());
//
//        if (teacherStatusDto.isDuty()) {
//            teacherStatusDto.setWorkStart(teacher.getWorkStart());
//            teacherStatusDto.setWorkEnd(teacher.getWorkEnd());
//        } else {
//            teacherStatusDto.setDisturbStart(teacher.getDisturbStart());
//            teacherStatusDto.setDisturbEnd(teacher.getDisturbEnd());
//        }
//
//        return teacherStatusDto;
        Teacher teacher = findTeacherById(teacherUserId);
        if (teacher == null) {
            throw new NoSuchElementException("해당 선생님을 찾을 수 없습니다.");
        }

        TeacherStatusDto teacherStatusDto = new TeacherStatusDto();
        teacherStatusDto.setDuty(teacher.isDuty());
        teacherStatusDto.setWorkStart(teacher.getWorkStart());
        teacherStatusDto.setWorkEnd(teacher.getWorkEnd());
        teacherStatusDto.setDisturbStart(teacher.getDisturbStart());
        teacherStatusDto.setDisturbEnd(teacher.getDisturbEnd());

        return teacherStatusDto;
    }
}