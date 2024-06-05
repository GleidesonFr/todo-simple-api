package com.learning.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.todosimple.models.Task;
import com.learning.todosimple.models.User;
import com.learning.todosimple.repositories.TaskRepository;
import com.learning.todosimple.services.exceptions.DataBindingViolationException;
import com.learning.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private UserService userService;
    @Autowired
    private TaskRepository taskRepository;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
            "Task not found! id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    public List<Task> findAllById(Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
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
            throw new DataBindingViolationException("Não is possible delete exists relationed entities!");
        }
    }
}
