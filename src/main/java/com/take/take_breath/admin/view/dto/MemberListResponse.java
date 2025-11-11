package com.take.take_breath.admin.view.dto;

import com.take.take_breath.members.Member;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberListResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final Role role;
    private final Status status;
    private final LocalDateTime withdrawalRequestedAt;
    private final String withdrawalReason;

    // 역할 체크
    private final boolean isUserRole;
    private final boolean isCounselorRole;
    private final boolean isAdminRole;

    // 상태 체크
    private final boolean isActive;
    private final boolean isSuspended;
    private final boolean isPending;
    private final boolean isWithdrawal;

    public MemberListResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickName();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.withdrawalRequestedAt = member.getWithdrawalRequestedAt();
        this.withdrawalReason = member.getWithdrawalReason();

        // 역할 체크
        this.isUserRole = member.getRole() == Role.USER;
        this.isCounselorRole = member.getRole() == Role.COUNSELOR;
        this.isAdminRole = member.getRole() == Role.ADMIN;

        // 상태 체크
        this.isActive = member.getStatus() == Status.ACTIVE;
        this.isSuspended = member.getStatus() == Status.SUSPENDED;
        this.isPending = member.getStatus() == Status.PENDING;
        this.isWithdrawal = member.getStatus() == Status.WITHDRAWAL;
    }
}