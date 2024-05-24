package com.capstone.backend.domain.user.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users_logins")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_date", nullable = false)
    private LocalDate loginDate;

    @Column(name = "login_year", nullable = false)
    private int loginYear;

    @Column(name = "login_week", nullable = false)
    private int loginWeek;

    @Column(name = "count", nullable = false)
    private int count = 0; // 로그인 횟수를 기록, 기본값 0

    public void incrementCount() {
        this.count++;
    }
}
