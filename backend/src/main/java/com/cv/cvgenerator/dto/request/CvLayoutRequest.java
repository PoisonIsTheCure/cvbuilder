package com.cv.cvgenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CvLayoutRequest {

    @NotBlank(message = "Layout name is required")
    private String name;

    private String primaryColor;
    private String secondaryColor;
    private String fontFamily;
    private String templateCode;
    private List<String> sectionOrder;
    private List<String> visibleSections;
}
