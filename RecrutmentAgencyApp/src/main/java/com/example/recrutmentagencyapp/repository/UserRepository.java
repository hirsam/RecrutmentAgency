package com.example.recrutmentagencyapp.repository;

import com.example.recrutmentagencyapp.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
