package com.ToDoApp.controller;

import com.ToDoApp.model.Task;
import com.ToDoApp.model.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/tasks")
class TaskController {
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository taskRepository;

    TaskController(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks(){
     logger.warn("Exposing all the tasks");
     return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/test")
    void oldFashionedWay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getParameter("foo"));
        response.getWriter().println("Test old-fashioned way");
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
     logger.info("Custom pageable");
     return ResponseEntity.ok(taskRepository.findAll(page).getContent());
    }

    @GetMapping(value = "/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(taskRepository.findByDone(state));
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<?> getSingleTask(@PathVariable int id){
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @PutMapping("/{id}")
    ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        taskRepository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    taskRepository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<Task> addNewTask(@RequestBody @Valid Task toAdd){
        Task result = taskRepository.save(toAdd);
        return ResponseEntity.created(URI.create("/"+ result.getId())).body(result);
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<Task> toggleTask(@PathVariable int id){
        if(!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        taskRepository.findById(id)
                .ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }
}
