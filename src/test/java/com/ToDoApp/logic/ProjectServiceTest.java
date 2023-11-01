package com.ToDoApp.logic;

import com.ToDoApp.TaskConfigurationProperties;
import com.ToDoApp.model.ProjectsRepository;
import com.ToDoApp.model.TaskGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

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
        var mockGroupRepository = mock(TaskGroupRepository.class);
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(false);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        var toTest = new ProjectService(null, mockGroupRepository, mockConfig);

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
        var toTest = new ProjectService(null, null, mockConfig);

        //when
        var exception = catchThrowable(()-> toTest.createGroup(LocalDateTime.now(), 0 ));
        //then
       assertThat(exception)
               .isInstanceOf(IllegalArgumentException.class)
               .hasMessageContaining("id not found");
    }

    private TaskConfigurationProperties configurationReturn(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

}

