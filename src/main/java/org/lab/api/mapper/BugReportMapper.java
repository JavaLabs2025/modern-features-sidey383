package org.lab.api.mapper;

import org.lab.api.dto.bug.BugReportAnswer;
import org.lab.data.entity.BugReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BugReportMapper {

    @Mapping(target = "id", source = "bug_id")
    BugReportAnswer mapToAnswer(BugReport bugReport);

}
