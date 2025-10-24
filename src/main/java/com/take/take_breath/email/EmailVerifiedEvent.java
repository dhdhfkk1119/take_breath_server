package com.take.take_breath.email;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailVerifiedEvent extends ApplicationEvent {

    private final String email;

    public EmailVerifiedEvent(Object source, String email) {
        super(source);
        this.email = email;
    }
}