package com.learning.todosimple.services;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.todosimple.models.User;
import com.learning.todosimple.models.enums.ProfileEnum;
import com.learning.todosimple.repositories.UserRepository;
import com.learning.todosimple.services.exceptions.DataBindingViolationException;
import com.learning.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id); //optinal é uma classe que recebe e envia valores vazios, não nulos
        return user.orElseThrow( () -> new ObjectNotFoundException( //orElseThrow retorna uma exceção caso um objeto seja vazio - RuntimeException não encerra o programa
            "User not found! id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional //inserir dados no banco de dados por meio da atomicidade
    public User create(User obj){
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Not is possible delete, because exists relationed entities!");
        }
    }

}
