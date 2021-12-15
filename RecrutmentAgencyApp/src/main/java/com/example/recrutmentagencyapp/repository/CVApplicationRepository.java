package com.example.recrutmentagencyapp.repository;

import com.example.recrutmentagencyapp.entity.CVApplication;
import com.example.recrutmentagencyapp.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface CVApplicationRepository extends CrudRepository<CVApplication, Long> {
    CVApplication findByUser(User user);
    void deleteByUser(User user);
    boolean existsByUser(User user);
    void deleteAllByUser(User user);
}
