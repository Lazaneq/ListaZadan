package com.ToDoApp.controller;

import com.ToDoApp.model.Task;
import com.ToDoApp.model.TaskRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.List;


@RestController
class TaskController {
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository taskRepository;

    TaskController(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/tasks", params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks(){
     logger.warn("Exposing all the tasks");
     return ResponseEntity.ok(taskRepository.findAll());
    }
    @GetMapping(value = "/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
     logger.info("Custom pageable");
     return ResponseEntity.ok(taskRepository.findAll(page).getContent());
    }

    @GetMapping(value = "/tasks/{id}")
    ResponseEntity<?> getSingleTask(@PathVariable int id){
//        if(!taskRepository.existsById(id)){
//            return ResponseEntity.notFound().build();
//        }else{
//            return ResponseEntity.ok(taskRepository.findById(id));
//        }
//        MÓJ KOD
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @PutMapping("/tasks/{id}")
    ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        toUpdate.setId(id);
        taskRepository.save(toUpdate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks")
    ResponseEntity<Task> addNewTask(@RequestBody @Valid Task toAdd){
//        taskRepository.save(toAdd);
//        return ResponseEntity.created(URI.create("/tasks"+toAdd.getId())).build();
//     Moje rozwiązanie
        Task result = taskRepository.save(toAdd);
        return ResponseEntity.created(URI.create("/"+ result.getId())).body(result);
    }
}
