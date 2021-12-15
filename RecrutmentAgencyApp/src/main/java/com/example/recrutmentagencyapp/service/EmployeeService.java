package com.example.recrutmentagencyapp.service;

import com.example.recrutmentagencyapp.entity.CVApplication;
import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.entity.Vacancy;
import com.example.recrutmentagencyapp.repository.UserRepository;
import com.example.recrutmentagencyapp.repository.VacancyRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private UserRepository userRepository;

    private VacancyRepository vacancyRepository;

    public EmployeeService(UserRepository userRepository, VacancyRepository vacancyRepository) {
        this.userRepository = userRepository;
        this.vacancyRepository = vacancyRepository;
    }

    public void saveVacancy(Vacancy vacancy) {
        var currentUser = getCurrentUser();

        vacancy.setUser(currentUser);
        currentUser.setVacancy(vacancy);
        vacancyRepository.save(vacancy);
        userRepository.save(currentUser);
    }

    public List<Vacancy> findVacancies(CVApplication cvApplication) {
        var allVacancies = vacancyRepository.findAll();
        return findAppropriateVacancies(allVacancies, cvApplication);
    }

    public List<Vacancy> getCurrentUserVacancies() {
        var currentUser = getCurrentUser();
        return vacancyRepository.findAllByUser(currentUser);
    }

    private List<Vacancy> findAppropriateVacancies(List<Vacancy> vacancies, CVApplication cvApplication) {
        System.out.println();
        return vacancies.stream().filter(vacancy -> {
            return cvApplication.getExpectedSalary() >= vacancy.getLowerWageLimit()
                && cvApplication.getExpectedSalary() <= vacancy.getUpperWageLimit()
                && cvApplication.getSkills().equals(vacancy.getDesiredApplicantSkills());
        }).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        return userRepository.findByUsername(username);
    }
}
