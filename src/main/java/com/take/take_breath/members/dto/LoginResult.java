package com.take.take_breath.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
    private final String token;
    private final boolean needWithdrawalConfirm;
    private final Long daysLeft;
}