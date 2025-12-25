package org.lab.api.mapper;

import org.lab.api.dto.ProjectAnswer;
import org.lab.api.dto.ProjectUserAnswer;
import org.lab.api.dto.ProjectUsersAnswer;
import org.lab.api.dto.ProjectWithRoleAnswer;
import org.lab.data.entity.Project;
import org.lab.data.entity.ProjectUser;
import org.lab.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(uses = UserMapper.class)
public interface ProjectMapper {

    @Mapping(target = "project", source = "projectUser.project")
    @Mapping(target = "projectRole", source = "projectUser.projectRole")
    default ProjectWithRoleAnswer mapToAnswer(ProjectUser projectUser, Project project, User projectManager, User teamLead) {
        return new ProjectWithRoleAnswer(
                mapToAnswer(project, projectManager, teamLead),
                projectUser.projectRole()
        );
    }

    @Mapping(target = "manager", source = "projectManager")
    @Mapping(target = "teamLead", source = "teamLead")
    @Mapping(target = "id", source = "project.projectId")
    ProjectAnswer mapToAnswer(Project project, User projectManager, User teamLead);

    default ProjectUsersAnswer mapToAnswer(Project project, User projectManager, User teamLead, Collection<ProjectUserAnswer> users) {
        return new ProjectUsersAnswer(
                users,
                mapToAnswer(project, projectManager, teamLead)
        );
    }

    @Mapping(target = "user", source = "user")
    @Mapping(target = "role", source = "projectUser.projectRole")
    ProjectUserAnswer mapToAnswer(User user, ProjectUser projectUser);

}
