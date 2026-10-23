package com.posadskiy.skillrepeater.web.controller;

import com.posadskiy.skillrepeater.api.dto.RepeatHistoryPageDto;
import com.posadskiy.skillrepeater.api.dto.ReviewRequest;
import com.posadskiy.skillrepeater.api.dto.SkillDto;
import com.posadskiy.skillrepeater.api.dto.SkillPageDto;
import com.posadskiy.skillrepeater.core.mapper.dto.RepeatHistoryDtoMapper;
import com.posadskiy.skillrepeater.core.mapper.dto.SkillDtoMapper;
import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.RepeatHistory;
import com.posadskiy.skillrepeater.core.model.Skill;
import com.posadskiy.skillrepeater.core.service.RepeatHistoryService;
import com.posadskiy.skillrepeater.core.service.ReviewService;
import com.posadskiy.skillrepeater.core.service.SkillService;
import com.posadskiy.skillrepeater.web.security.SkillAuthorization;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v0/skills")
@Tag(name = "Skills")
public class SkillController {

    private final SkillService skillService;
    private final ReviewService reviewService;
    private final RepeatHistoryService repeatHistoryService;
    private final SkillDtoMapper skillDtoMapper;
    private final RepeatHistoryDtoMapper repeatHistoryDtoMapper;
    private final SkillAuthorization skillAuthorization;

    public SkillController(
            SkillService skillService,
            ReviewService reviewService,
            RepeatHistoryService repeatHistoryService,
            SkillDtoMapper skillDtoMapper,
            RepeatHistoryDtoMapper repeatHistoryDtoMapper,
            SkillAuthorization skillAuthorization) {
        this.skillService = skillService;
        this.reviewService = reviewService;
        this.repeatHistoryService = repeatHistoryService;
        this.skillDtoMapper = skillDtoMapper;
        this.repeatHistoryDtoMapper = repeatHistoryDtoMapper;
        this.skillAuthorization = skillAuthorization;
    }

    @Get
    @NewSpan
    public SkillPageDto list(
            Authentication authentication,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size) {
        String userId = skillAuthorization.currentUserId(authentication);
        return toPage(skillService.pageByUser(userId, page, size));
    }

    @Get("/due")
    @NewSpan
    public List<SkillDto> due(
            Authentication authentication,
            @QueryValue(defaultValue = "10") int limit) {
        String userId = skillAuthorization.currentUserId(authentication);
        return skillService.due(userId, limit).stream().map(skillDtoMapper::mapToDto).toList();
    }

    @Get("/{id}")
    @NewSpan
    public SkillDto get(Authentication authentication, @PathVariable String id) {
        return skillDtoMapper.mapToDto(skillAuthorization.requireSkill(authentication, id));
    }

    @Post
    @Status(HttpStatus.CREATED)
    @NewSpan
    public SkillDto create(Authentication authentication, @Body @Valid SkillDto skillDto) {
        Skill skill = skillDtoMapper.mapFromDto(skillDto);
        skillAuthorization.assignOwner(authentication, skill);
        boolean learnNow = Boolean.TRUE.equals(skillDto.learnNow());
        return skillDtoMapper.mapToDto(skillService.addSkill(skill, learnNow));
    }

    @Post("/batch")
    @Status(HttpStatus.CREATED)
    @NewSpan
    public List<SkillDto> createAll(Authentication authentication, @Body @Valid List<SkillDto> skills) {
        return skills.stream().map(skill -> create(authentication, skill)).toList();
    }

    @Put("/{id}")
    @NewSpan
    public SkillDto update(Authentication authentication, @PathVariable String id, @Body @Valid SkillDto skillDto) {
        skillAuthorization.requireSkill(authentication, id);
        Skill skill = skillDtoMapper.mapFromDto(skillDto);
        skill.setId(id);
        skillAuthorization.assignOwner(authentication, skill);
        return skillDtoMapper.mapToDto(skillService.editSkill(skill));
    }

    @Delete
    @Status(HttpStatus.NO_CONTENT)
    @NewSpan
    public void deleteAll(Authentication authentication) {
        skillService.deleteAllByUser(skillAuthorization.currentUserId(authentication));
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @NewSpan
    public void delete(Authentication authentication, @PathVariable String id) {
        skillAuthorization.requireSkill(authentication, id);
        skillService.deleteById(id);
    }

    @Post("/{id}/reviews")
    @NewSpan
    public SkillDto review(Authentication authentication, @PathVariable String id, @Body @Valid ReviewRequest request) {
        skillAuthorization.requireSkill(authentication, id);
        return skillDtoMapper.mapToDto(reviewService.review(
                skillAuthorization.currentUserId(authentication),
                id,
                request.rating()
        ));
    }

    @Get("/{id}/history")
    @NewSpan
    public RepeatHistoryPageDto history(
            Authentication authentication,
            @PathVariable String id,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size) {
        skillAuthorization.requireSkill(authentication, id);
        return toHistoryPage(repeatHistoryService.pageBySkill(id, page, size));
    }

    @Get("/reviews")
    @NewSpan
    public RepeatHistoryPageDto reviews(
            Authentication authentication,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size) {
        String userId = skillAuthorization.currentUserId(authentication);
        return toHistoryPage(repeatHistoryService.pageByUser(userId, page, size));
    }

    private SkillPageDto toPage(PageResult<Skill> page) {
        return new SkillPageDto(
                page.items().stream().map(skillDtoMapper::mapToDto).toList(),
                page.page(),
                page.size(),
                page.total()
        );
    }

    private RepeatHistoryPageDto toHistoryPage(PageResult<RepeatHistory> page) {
        return new RepeatHistoryPageDto(
                page.items().stream().map(repeatHistoryDtoMapper::mapToDto).toList(),
                page.page(),
                page.size(),
                page.total()
        );
    }
}
