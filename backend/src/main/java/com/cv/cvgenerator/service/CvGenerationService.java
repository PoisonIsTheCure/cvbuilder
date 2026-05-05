package com.cv.cvgenerator.service;

import com.cv.cvgenerator.entity.*;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.MasterCvRepository;
import com.cv.cvgenerator.repository.ProfileRepository;
import com.cv.cvgenerator.repository.TailoredCvRepository;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvGenerationService {

    private static final String TEMPLATE_PATH = "/templates/cv/cv-template.html";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");

    private static final String DEFAULT_PRIMARY   = "#2563EB";
    private static final String DEFAULT_SECONDARY = "#EFF6FF";
    private static final String DEFAULT_FONT      = "Arial";

    private final TailoredCvRepository tailoredCvRepository;
    private final MasterCvRepository masterCvRepository;
    private final ProfileRepository profileRepository;

    // ── Generate from TailoredCv ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateFromTailoredCv(Long userId, Long tailoredCvId) {
        TailoredCv tailoredCv = tailoredCvRepository.findByIdWithFullDetails(tailoredCvId)
                .orElseThrow(() -> new ResourceNotFoundException("TailoredCv", tailoredCvId));

        if (!tailoredCv.getUser().getId().equals(userId)) {
            throw new com.cv.cvgenerator.exception.UnauthorizedException(
                    "TailoredCv not accessible: " + tailoredCvId);
        }

        // Fetch profile from MasterCv
        Profile profile = profileRepository
                .findByMasterCvId(tailoredCv.getMasterCv().getId())
                .orElse(null);

        CvLayout layout = tailoredCv.getCvLayout();
        List<String> sectionOrder = layout != null && layout.getSectionOrder() != null
                ? layout.getSectionOrder()
                : List.of("SUMMARY", "EXPERIENCE", "PROJECTS", "EDUCATION", "SKILLS", "LANGUAGES");

        List<String> visibleSections = layout != null && layout.getVisibleSections() != null
                ? layout.getVisibleSections()
                : sectionOrder;

        CvData cvData = CvData.builder()
                .user(tailoredCv.getUser())
                .profile(profile)
                .experiences(tailoredCv.getExperiences())
                .projects(tailoredCv.getProjects())
                .skills(tailoredCv.getSkills())
                .educations(tailoredCv.getMasterCv().getEducations())
                .languages(tailoredCv.getMasterCv().getLanguages())
                .primaryColor(layout != null ? layout.getPrimaryColor() : DEFAULT_PRIMARY)
                .secondaryColor(layout != null ? layout.getSecondaryColor() : DEFAULT_SECONDARY)
                .fontFamily(layout != null ? layout.getFontFamily() : DEFAULT_FONT)
                .sectionOrder(sectionOrder)
                .visibleSections(visibleSections)
                .build();

        return generatePdf(cvData);
    }

    // ── Generate from MasterCv (full CV, no filtering) ───────────────────────

    @Transactional(readOnly = true)
    public byte[] generateFromMasterCv(Long userId) {
        MasterCv masterCv = masterCvRepository.findByUserIdWithFullDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MasterCv not found for user: " + userId));

        Profile profile = profileRepository
                .findByMasterCvId(masterCv.getId())
                .orElse(null);

        List<String> defaultSections = List.of(
                "SUMMARY", "EXPERIENCE", "PROJECTS", "EDUCATION", "SKILLS", "LANGUAGES");

        CvData cvData = CvData.builder()
                .user(masterCv.getUser())
                .profile(profile)
                .experiences(masterCv.getExperiences())
                .projects(masterCv.getProjects())
                .skills(masterCv.getSkills())
                .educations(masterCv.getEducations())
                .languages(masterCv.getLanguages())
                .primaryColor(DEFAULT_PRIMARY)
                .secondaryColor(DEFAULT_SECONDARY)
                .fontFamily(DEFAULT_FONT)
                .sectionOrder(defaultSections)
                .visibleSections(defaultSections)
                .build();

        return generatePdf(cvData);
    }

    // ── Core PDF generation ───────────────────────────────────────────────────

    private byte[] generatePdf(CvData data) {
        String html = buildHtml(data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, outputStream, properties);
            log.info("PDF generated successfully for user: {}", data.getUser().getId());
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("PDF generation failed: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    // ── HTML builder ──────────────────────────────────────────────────────────

    private String buildHtml(CvData data) {
        String template = loadTemplate();

        // Inject layout variables
        String html = template
                .replace("{{PRIMARY_COLOR}}",   nvl(data.getPrimaryColor(),   DEFAULT_PRIMARY))
                .replace("{{SECONDARY_COLOR}}", nvl(data.getSecondaryColor(), DEFAULT_SECONDARY))
                .replace("{{FONT_FAMILY}}",     nvl(data.getFontFamily(),     DEFAULT_FONT));

        // Inject header
        User user = data.getUser();
        html = html.replace("{{FULL_NAME}}",
                user.getFirstName() + " " + user.getLastName());

        Profile profile = data.getProfile();
        html = resolveConditional(html, "TITLE",
                profile != null ? profile.getTitle() : null);
        html = resolveConditional(html, "PHONE",
                profile != null ? profile.getPhone() : null);
        html = resolveConditional(html, "LOCATION",
                profile != null ? profile.getLocation() : null);
        html = resolveConditional(html, "LINKEDIN",
                profile != null ? profile.getLinkedinUrl() : null);
        html = resolveConditional(html, "GITHUB",
                profile != null ? profile.getGithubUrl() : null);
        html = resolveConditional(html, "PORTFOLIO",
                profile != null ? profile.getPortfolioUrl() : null);

        // Build and inject sections in layout order
        StringBuilder sections = new StringBuilder();
        for (String section : data.getSectionOrder()) {
            if (!data.getVisibleSections().contains(section)) continue;

            switch (section.toUpperCase()) {
                case "SUMMARY"    -> buildSummarySection(sections, profile);
                case "EXPERIENCE" -> buildExperienceSection(sections, data.getExperiences());
                case "EDUCATION"  -> buildEducationSection(sections, data.getEducations());
                case "PROJECTS"   -> buildProjectsSection(sections, data.getProjects());
                case "SKILLS"     -> buildSkillsSection(sections, data.getSkills());
                case "LANGUAGES"  -> buildLanguagesSection(sections, data.getLanguages());
            }
        }
        html = html.replace("{{SECTIONS}}", sections.toString());

        return html;
    }

    // ── Section builders ──────────────────────────────────────────────────────

    private void buildSummarySection(StringBuilder sb, Profile profile) {
        if (profile == null || profile.getSummary() == null) return;
        sb.append("""
                <div class="section">
                    <div class="section-title">Profile</div>
                    <div class="summary">%s</div>
                </div>
                """.formatted(profile.getSummary()));
    }

    private void buildExperienceSection(StringBuilder sb, List<Experience> experiences) {
        if (experiences == null || experiences.isEmpty()) return;
        sb.append("<div class=\"section\"><div class=\"section-title\">Experience</div>");
        for (Experience e : experiences) {
            String dates = formatDateRange(
                    e.getStartDate() != null ? e.getStartDate().format(DATE_FORMAT) : "",
                    e.isCurrent() ? "Present" : (e.getEndDate() != null ? e.getEndDate().format(DATE_FORMAT) : ""));
            sb.append("""
                    <div class="entry">
                        <div class="entry-header">
                            <div>
                                <div class="entry-title">%s</div>
                                <div class="entry-subtitle">%s</div>
                            </div>
                            <div class="entry-date">%s</div>
                        </div>
                        %s
                    </div>
                    """.formatted(
                    e.getRole(), e.getCompany(), dates,
                    e.getDescription() != null
                            ? "<div class=\"entry-description\">" + e.getDescription() + "</div>"
                            : ""
            ));
        }
        sb.append("</div>");
    }

    private void buildEducationSection(StringBuilder sb, List<Education> educations) {
        if (educations == null || educations.isEmpty()) return;
        sb.append("<div class=\"section\"><div class=\"section-title\">Education</div>");
        for (Education e : educations) {
            String dates = formatDateRange(
                    e.getStartDate() != null ? e.getStartDate().format(DATE_FORMAT) : "",
                    e.isCurrent() ? "Present" : (e.getEndDate() != null ? e.getEndDate().format(DATE_FORMAT) : ""));
            sb.append("""
                    <div class="entry">
                        <div class="entry-header">
                            <div>
                                <div class="entry-title">%s</div>
                                <div class="entry-subtitle">%s%s</div>
                            </div>
                            <div class="entry-date">%s</div>
                        </div>
                    </div>
                    """.formatted(
                    e.getInstitution(),
                    e.getDegree(),
                    e.getFieldOfStudy() != null ? " — " + e.getFieldOfStudy() : "",
                    dates
            ));
        }
        sb.append("</div>");
    }

    private void buildProjectsSection(StringBuilder sb, List<Project> projects) {
        if (projects == null || projects.isEmpty()) return;
        sb.append("<div class=\"section\"><div class=\"section-title\">Projects</div>");
        for (Project p : projects) {
            sb.append("""
                    <div class="entry">
                        <div class="entry-title">%s</div>
                        %s
                        %s
                        %s
                    </div>
                    """.formatted(
                    p.getName(),
                    p.getDescription() != null
                            ? "<div class=\"entry-description\">" + p.getDescription() + "</div>"
                            : "",
                    p.getTechStack() != null
                            ? "<div class=\"project-tech\">" + p.getTechStack() + "</div>"
                            : "",
                    p.getProjectUrl() != null
                            ? "<div class=\"project-link\">" + p.getProjectUrl() + "</div>"
                            : ""
            ));
        }
        sb.append("</div>");
    }

    private void buildSkillsSection(StringBuilder sb, List<Skill> skills) {
        if (skills == null || skills.isEmpty()) return;
        sb.append("<div class=\"section\"><div class=\"section-title\">Skills</div>");
        sb.append("<div class=\"skills-grid\">");
        for (Skill s : skills) {
            sb.append("<span class=\"skill-tag\">").append(s.getName()).append("</span>");
        }
        sb.append("</div></div>");
    }

    private void buildLanguagesSection(StringBuilder sb, List<Language> languages) {
        if (languages == null || languages.isEmpty()) return;
        sb.append("<div class=\"section\"><div class=\"section-title\">Languages</div>");
        sb.append("<div class=\"languages-list\">");
        for (Language l : languages) {
            sb.append("""
                    <div class="language-item">
                        <span class="lang-name">%s</span>
                        %s
                    </div>
                    """.formatted(
                    l.getName(),
                    l.getProficiency() != null
                            ? "<span class=\"lang-level\"> — " + l.getProficiency() + "</span>"
                            : ""
            ));
        }
        sb.append("</div></div>");
    }

    // ── Template loader ───────────────────────────────────────────────────────

    private String loadTemplate() {
        try (InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (is == null) {
                throw new RuntimeException("CV template not found at: " + TEMPLATE_PATH);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CV template: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveConditional(String html, String key, String value) {
        if (value != null && !value.isBlank()) {
            return html
                    .replace("{{#if " + key + "}}", "")
                    .replace("{{/if}}", "")
                    .replace("{{" + key + "}}", value);
        }
        // Remove the entire conditional block
        return html.replaceAll("\\{\\{#if " + key + "\\}\\}.*?\\{\\{/if\\}\\}", "");
    }

    private String formatDateRange(String start, String end) {
        if (start.isBlank() && end.isBlank()) return "";
        if (start.isBlank()) return end;
        if (end.isBlank()) return start;
        return start + " – " + end;
    }

    private String nvl(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    // ── Internal data carrier ─────────────────────────────────────────────────

    @lombok.Builder
    @lombok.Getter
    private static class CvData {
        private User user;
        private Profile profile;
        private List<Experience> experiences;
        private List<Education> educations;
        private List<Project> projects;
        private List<Skill> skills;
        private List<Language> languages;
        private String primaryColor;
        private String secondaryColor;
        private String fontFamily;
        private List<String> sectionOrder;
        private List<String> visibleSections;
    }
}