package com.johnfnash.learn.redis.shiro.session.shiro;

import org.apache.shiro.util.SimpleByteSource;

import java.io.Serializable;

public class SerializableByteSource extends SimpleByteSource implements Serializable {

    private static final long serialVersionUID = 1L;

    public SerializableByteSource(String string) {
        super(string);
    }

}