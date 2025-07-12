package com.johnfnash.learn.redis.session.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String password;

    public User() {
    }

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

}
