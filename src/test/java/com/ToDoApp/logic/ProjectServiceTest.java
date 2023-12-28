package com.ToDoApp.logic;

import com.ToDoApp.TaskConfigurationProperties;
import com.ToDoApp.model.ProjectsRepository;
import com.ToDoApp.model.TaskGroup;
import com.ToDoApp.model.TaskGroupRepository;
import com.ToDoApp.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_ThrowsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        TaskConfigurationProperties mockConfig = configurationReturn(false);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(false);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        var toTest = new ProjectService(null, mockGroupRepository, null, mockConfig);

        //when
        var exception = catchThrowable(()-> toTest.createGroup(LocalDateTime.now(), 0 ));
        //then
        assertThat(exception).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> toTest.createGroup(LocalDateTime.now(), 0)).isInstanceOf(IllegalStateException.class);
        assertThatIllegalStateException().isThrownBy(()->toTest.createGroup(LocalDateTime.now(), 0));
        assertTrue(mockGroupRepository.existsByDoneIsFalseAndProject_Id(500));

    }
    @Test
    @DisplayName("should throw IllegalStateException when configured ok and no projects for given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectsRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        var mockConfig = configurationReturn(true);
        //system under test
        var toTest = new ProjectService(mockRepository, null,null, mockConfig);

        //when
        var exception = catchThrowable(()-> toTest.createGroup(LocalDateTime.now(), 0 ));
        //then
       assertThat(exception)
               .isInstanceOf(IllegalArgumentException.class)
               .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and no groups and projects for given id")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectsRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositortReturning(false);
        //and
        var mockConfig = configurationReturn(true);
        //system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository,null, mockConfig);

        //when
        var exception = catchThrowable(()-> toTest.createGroup(LocalDateTime.now(), 0 ));
        //then
       assertThat(exception)
               .isInstanceOf(IllegalArgumentException.class)
               .hasMessageContaining("id not found");
    }

//
//    @Test
//    @DisplayName("should create a new group from project")
//    void createGroup_configOk_existingProject_createsAndSavesGroup(){
//        //given
//        var today = LocalDateTime.now();
//        //and
//        var project = projectWith("bar", Set.of(-1, -2));
//        var mockRepository = mock(ProjectsRepository.class);
//        when(mockRepository.findById(anyInt()))
//                .thenReturn(Optional.of(project));
//        //and
//        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
//        var serviceWithInMemRepo = dummyGroupService(inMemoryGroupRepo);
//        int countBeforeCall = inMemoryGroupRepo.count();
//        //and
//        TaskConfigurationProperties mockConfig = configurationReturn(true);
//        //system under test
//        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, serviceWithInMemRepo, mockConfig);
//
//        //when
//        GroupReadModel result = toTest.createGroup(today,1);
//
//        //then
//        assertThat(result.getDescription()).isEqualTo("bar");
//        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
//        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
//        assertThat(countBeforeCall + 1).isEqualTo(inMemoryGroupRepo.count());
//    }
//
//    private static TaskGroupService dummyGroupService(InMemoryGroupRepository inMemoryGroupRepo) {
//        return new TaskGroupService(inMemoryGroupRepo, null);
//    }

    private static TaskGroupRepository groupRepositortReturning(final boolean result ) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturn(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }


    private TaskGroupRepository inMemoryGroupRepository() {
        return new TaskGroupRepository() {
            private int index = 0;
            private Map<Integer, TaskGroup> map = new HashMap<>();

            @Override
            public List<TaskGroup> findAll() {
                return new ArrayList<>(map.values());
            }

            @Override
            public Optional<TaskGroup> findById(Integer id) {
                return Optional.ofNullable(map.get(id));
            }

            @Override
            public TaskGroup save(TaskGroup entity) {
                if (entity.getId() == 0) {
                    try {
                        TaskGroup.class.getDeclaredField("id").set(entity, ++index);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                map.put(index, entity);
                return entity;
            }

            @Override
            public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
                return map.values().stream()
                        .filter(taskGroup -> !taskGroup.isDone())
                        .anyMatch(taskGroup -> taskGroup.getProject()!=null && taskGroup.getProject().getId() == projectId) ;
            }

        };
    }
}

