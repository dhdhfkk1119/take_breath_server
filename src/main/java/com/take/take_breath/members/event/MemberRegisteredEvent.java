package com.take.take_breath.members.event;

import com.take.take_breath.members.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberRegisteredEvent extends ApplicationEvent {

    private final Member member;
    private final String email;

    public MemberRegisteredEvent(Object source, Member member, String email) {
        super(source);
        this.member = member;
        this.email = email;
    }
}