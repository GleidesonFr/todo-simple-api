package com.learning.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.todosimple.models.User;
import com.learning.todosimple.repositories.TaskRepository;
import com.learning.todosimple.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id); //optinal é uma classe que recebe e envia valores vazios, não nulos
        return user.orElseThrow( () -> new RuntimeException( //orElseThrow retorna uma exceção caso um objeto seja vazio - RuntimeException não encerra o programa
            "User not found! id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional //inserir dados no banco de dados por meio da atomicidade
    public User create(User obj){
        obj.setId(null);
        obj = this.userRepository.save(obj);
        this.taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        return this.userRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Not is possible delete, because exists relationed entities!");
        }
    }

}
