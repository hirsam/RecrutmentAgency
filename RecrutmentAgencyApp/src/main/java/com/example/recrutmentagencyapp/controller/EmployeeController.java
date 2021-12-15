package com.example.recrutmentagencyapp.controller;

import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.entity.Vacancy;
import com.example.recrutmentagencyapp.service.EmployeeService;
import com.example.recrutmentagencyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private UserService userService;

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(UserService userService, EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("/employeeRegistration")
    public String getEmployeeRegistrationPage() {
        return "employee/employee-registration";
    }

    @PostMapping(value = "/registerEmployee")
    public String registerEmployee(@ModelAttribute @Valid User user, Model model, BindingResult bindingResult) {
        var registrationResult = userService.saveUser(user, "EMPLOYEE");

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors().get(0).getObjectName());
            return "employee/employee-registration";
        }

        if (!registrationResult) {
            model.addAttribute("message", "User exists!");
            return "employee/employee-registration";
        }

        return "redirect:/login";
    }
    @GetMapping("/employeeHomePage")
    public String getEmployeeHomePage(Model model) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        var currentUser = userService.getUserByUsername(username);
        var currentUserApplications = employeeService.getCurrentUserVacancies();
        model.addAttribute("vacancies", currentUserApplications);
        model.addAttribute("user", currentUser);
        return "employee/employee-home";
    }

    @GetMapping("/createApplication")
    public String getApplicationFillingPage() {
        return "employee/application-fill";
    }

    @PostMapping("/createApplication")
    public String createApplicationForAnEmployee(@ModelAttribute @Valid Vacancy vacancy, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "employee/application-fill";
        }

        employeeService.saveVacancy(vacancy);
        return "redirect:/employee/employeeHomePage";
    }



}
