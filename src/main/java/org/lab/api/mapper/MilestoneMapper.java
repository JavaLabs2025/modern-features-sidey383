package org.lab.api.mapper;

import org.lab.api.dto.milestone.MilestoneAnswer;
import org.lab.data.entity.Milestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MilestoneMapper {

    @Mapping(target = "id", source = "milestoneId")
    MilestoneAnswer toAnswer(Milestone milestone);

}
