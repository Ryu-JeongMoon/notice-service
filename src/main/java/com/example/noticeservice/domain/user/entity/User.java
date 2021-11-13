package com.example.noticeservice.domain.user.entity;

import com.example.noticeservice.util.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 기본 생성 USER
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Builder
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setPasswordAs(String password) {
        this.password = password;
    }

    public void changeRole(Role role) {
        this.role = role;
    }
}
