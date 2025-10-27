package com.take.take_breath.community.community_event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeEvent {
    private Long postMemberId;
    private String postTitle;
    private Long likerMemberId;
}
