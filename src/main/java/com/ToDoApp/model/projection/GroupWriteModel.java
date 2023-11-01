package com.ToDoApp.model.projection;

import com.ToDoApp.model.Task;
import com.ToDoApp.model.TaskGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupWriteModel {
    private String description;
    private Set<GroupTaskWriteModel>  tasks;

    public TaskGroup toGroup(){
     var result = new TaskGroup();
     result.setDescription(description);
     result.setTasks(
             tasks.stream()
                     .map(GroupTaskWriteModel::ToTask)
                     .collect(Collectors.toSet())
     );
        return result;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<GroupTaskWriteModel> getTasks() {
        return tasks;
    }

    public void setTasks(Set<GroupTaskWriteModel> tasks) {
        this.tasks = tasks;
    }
}