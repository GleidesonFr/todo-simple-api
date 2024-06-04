package com.learning.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.todosimple.models.Task;
import com.learning.todosimple.models.User;
import com.learning.todosimple.repositories.TaskRepository;

@Service
public class TaskService {
    
    @Autowired
    private UserService userService;
    @Autowired
    private TaskRepository taskRepository;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
            "Task not found! id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);    
        } catch (Exception e) {
            throw new RuntimeException("Não is possible delete exists relationed entities!");
        }
    }
}
