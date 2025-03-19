package com.teamsphere.service;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PositionService extends GenericService<PositionEntity, PositionDto> {

    Page<PositionDto> find(PositionSearchRequest searchRequest, Pageable pageable);

}
