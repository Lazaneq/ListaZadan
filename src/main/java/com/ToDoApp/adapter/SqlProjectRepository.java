package com.ToDoApp.adapter;

import com.ToDoApp.model.Project;
import com.ToDoApp.model.ProjectsRepository;
import com.ToDoApp.model.TaskGroup;
import com.ToDoApp.model.TaskGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface SqlProjectRepository extends ProjectsRepository, JpaRepository<Project, Integer> {
    @Override
    @Query("select distinct p from Project p join fetch p.steps")
    List<Project> findAll();
}
