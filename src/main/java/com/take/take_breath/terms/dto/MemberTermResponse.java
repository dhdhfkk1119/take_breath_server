package com.take.take_breath.terms.dto;

import com.take.take_breath.terms.Terms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberTermResponse {
    private Long id;
    private String title;
    private String content;
    private boolean required;


    public MemberTermResponse(Terms terms){
        this.id = terms.getId();
        this.title = terms.getTitle();
        this.content = terms.getContent();
        this.required = terms.isRequired();
    }
}
