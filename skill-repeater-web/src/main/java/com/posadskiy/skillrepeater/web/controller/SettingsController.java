package com.posadskiy.skillrepeater.web.controller;

import com.posadskiy.skillrepeater.api.dto.UserSettingsDto;
import com.posadskiy.skillrepeater.core.service.UserSettingsService;
import com.posadskiy.skillrepeater.core.storage.db.entity.UserSettingsEntity;
import com.posadskiy.skillrepeater.web.security.SkillAuthorization;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v0/settings")
public class SettingsController {

    private final UserSettingsService userSettingsService;
    private final SkillAuthorization skillAuthorization;

    public SettingsController(UserSettingsService userSettingsService, SkillAuthorization skillAuthorization) {
        this.userSettingsService = userSettingsService;
        this.skillAuthorization = skillAuthorization;
    }

    @Get
    public UserSettingsDto get(Authentication authentication) {
        return toDto(userSettingsService.get(skillAuthorization.currentUserId(authentication)));
    }

    @Put
    public UserSettingsDto save(Authentication authentication, @Body @Valid UserSettingsDto settings) {
        return toDto(userSettingsService.save(
                skillAuthorization.currentUserId(authentication),
                settings.zoneId(),
                settings.reminderHour()
        ));
    }

    private UserSettingsDto toDto(UserSettingsEntity entity) {
        return new UserSettingsDto(entity.getZoneId(), entity.getReminderHour());
    }
}
