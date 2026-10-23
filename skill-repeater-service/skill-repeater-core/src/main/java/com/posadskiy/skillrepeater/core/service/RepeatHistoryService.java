package com.posadskiy.skillrepeater.core.service;

import com.posadskiy.skillrepeater.core.model.PageResult;
import com.posadskiy.skillrepeater.core.model.RepeatHistory;

public interface RepeatHistoryService {
    PageResult<RepeatHistory> pageBySkill(String skillId, int page, int size);

    PageResult<RepeatHistory> pageByUser(String userId, int page, int size);
}
