package com.ToDoApp.logic;

import com.ToDoApp.TaskConfigurationProperties;
import com.ToDoApp.model.ProjectsRepository;
import com.ToDoApp.model.TaskGroupRepository;
import com.ToDoApp.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {
    @Bean
    ProjectService projectService(
            final ProjectsRepository repository,
            final TaskGroupRepository taskGroupRepository,
            final TaskGroupService taskGroupService,
            final TaskConfigurationProperties config
            ){
        return new ProjectService(repository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService groupService(
            final TaskGroupRepository repository,
            final TaskRepository taskRepository
    ){
        return new TaskGroupService(repository, taskRepository);
    }
}
