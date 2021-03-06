package com.hd.im.handler.annotation;

import com.im.core.proto.HDIMProtocol;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Handler {
    HDIMProtocol.IMCommand value();
}
