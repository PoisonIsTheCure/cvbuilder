package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.CvLayoutRequest;
import com.cv.cvgenerator.dto.response.CvLayoutResponse;
import com.cv.cvgenerator.entity.CvLayout;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.CvLayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CvLayoutService {

    private final CvLayoutRepository cvLayoutRepository;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @Transactional
    public CvLayoutResponse create(CvLayoutRequest request) {
        CvLayout layout = CvLayout.builder()
                .name(request.getName())
                .primaryColor(request.getPrimaryColor())
                .secondaryColor(request.getSecondaryColor())
                .fontFamily(request.getFontFamily())
                .templateCode(request.getTemplateCode())
                .sectionOrder(request.getSectionOrder())
                .visibleSections(request.getVisibleSections())
                .build();
        return toResponse(cvLayoutRepository.save(layout));
    }

    @Transactional(readOnly = true)
    public CvLayoutResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<CvLayoutResponse> getAll() {
        return cvLayoutRepository.findAllByOrderByNameAsc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CvLayoutResponse update(Long id, CvLayoutRequest request) {
        CvLayout layout = findById(id);
        layout.setName(request.getName());
        layout.setPrimaryColor(request.getPrimaryColor());
        layout.setSecondaryColor(request.getSecondaryColor());
        layout.setFontFamily(request.getFontFamily());
        layout.setTemplateCode(request.getTemplateCode());
        layout.setSectionOrder(request.getSectionOrder());
        layout.setVisibleSections(request.getVisibleSections());
        return toResponse(cvLayoutRepository.save(layout));
    }

    @Transactional
    public void delete(Long id) {
        cvLayoutRepository.delete(findById(id));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public CvLayout findById(Long id) {
        return cvLayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CvLayout", id));
    }

    private CvLayoutResponse toResponse(CvLayout layout) {
        return CvLayoutResponse.builder()
                .id(layout.getId())
                .name(layout.getName())
                .primaryColor(layout.getPrimaryColor())
                .secondaryColor(layout.getSecondaryColor())
                .fontFamily(layout.getFontFamily())
                .templateCode(layout.getTemplateCode())
                .sectionOrder(layout.getSectionOrder())
                .visibleSections(layout.getVisibleSections())
                .build();
    }
}
