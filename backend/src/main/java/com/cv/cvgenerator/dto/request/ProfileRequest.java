package com.cv.cvgenerator.dto.request;

import lombok.Data;

@Data
public class ProfileRequest {

    private String title;
    private String summary;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
}
