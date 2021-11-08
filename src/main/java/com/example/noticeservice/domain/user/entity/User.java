package com.example.noticeservice.domain.user.entity;

import com.example.noticeservice.util.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private Role role;

    @Builder
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
