package com.cv.cvgenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvLayoutResponse {
    private Long id;
    private String name;
    private String primaryColor;
    private String secondaryColor;
    private String fontFamily;
    private String templateCode;
    private List<String> sectionOrder;
    private List<String> visibleSections;
}
