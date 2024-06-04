package com.learning.todosimple.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.learning.todosimple.models.User;
import com.learning.todosimple.models.User.CreateUser;
import com.learning.todosimple.models.User.UpdateUser;
import com.learning.todosimple.services.UserService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/user") //mapeia as requisições
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}") //mapeia as requisições GET
    public ResponseEntity<User> findById(@PathVariable Long id){ //PathVariable associa a variável de entrada ao caminho da requisição
        User obj = this.userService.findById(id);
        return ResponseEntity.ok().body(obj); //Retorna um código 200 e o objeto no corpo da requisição
    }

    @PostMapping
    @Validated(CreateUser.class)
    public ResponseEntity<Void> create(@Valid @RequestBody User obj){
        this.userService.create(obj);
        //O comando abaixo constroi uma uri com base ao objeto vindo de uma requisição atual
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated(UpdateUser.class)
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody User obj){
        obj.setId(id);
        this.userService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
