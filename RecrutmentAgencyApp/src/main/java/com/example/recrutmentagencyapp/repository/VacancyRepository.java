package com.example.recrutmentagencyapp.repository;

import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.entity.Vacancy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
    List<Vacancy> findAllByUser(User user);
    List<Vacancy> findAll();
}
