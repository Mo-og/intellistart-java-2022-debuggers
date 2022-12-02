package com.intellias.intellistart.interviewplanning.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.context.junit.jupiter.DisabledIf;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@DisabledIf(expression = "#{environment.acceptsProfiles('offline')}", loadContext = true,
    reason = "Disabled for offline profile")
public @interface DisabledWhenOffline {

}
