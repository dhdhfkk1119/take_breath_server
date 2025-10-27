package com.take.take_breath._core._utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {
    private String rootDir;   // ./uploads/
    private String corpDir;   // corp-images/
    private String memberDir; // member-images/
}