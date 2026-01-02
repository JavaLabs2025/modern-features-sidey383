package org.lab.api.mapper;

import org.lab.api.dto.bug.BugReportAnswer;
import org.lab.data.entity.BugReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BugReportMapper {

    @Mapping(target = "id", source = "bugId")
    BugReportAnswer mapToAnswer(BugReport bugReport);

}
