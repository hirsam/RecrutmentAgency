package com.example.recrutmentagencyapp.controller;

import com.example.recrutmentagencyapp.entity.CVApplication;
import com.example.recrutmentagencyapp.entity.Order;
import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.exception.OrderExistsException;
import com.example.recrutmentagencyapp.service.ApplicantService;
import com.example.recrutmentagencyapp.service.EmployeeService;
import com.example.recrutmentagencyapp.service.UserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    private UserService userService;

    private EmployeeService employeeService;

    private ApplicantService applicantService;

    @Autowired
    public ApplicantController(UserService userService, EmployeeService employeeService, ApplicantService applicantService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.applicantService = applicantService;
    }

    @GetMapping("/applicantRegistration")
    public String getApplicantRegistrationPage() {
        return "applicant/applicant-registration";
    }

    @PostMapping(value = "/registerApplicant")
    public String registerApplicant(@ModelAttribute @Valid User user, Model model, BindingResult bindingResult) {
        var registrationResult = userService.saveUser(user, "APPLICANT");

        if (bindingResult.hasErrors()) {
            return "applicant/applicant-registration";
        }

        if (!registrationResult) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/applicantHomePage")
    public String getApplicantHomePage(Model model) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());
        CVApplication cvApplication = userService.findCVByCurrentUser();
        var currentUser = userService.getUserByUsername(username);
        System.out.println("cur user role: " + currentUser.getRoles());
        model.addAttribute("user", currentUser);
        model.addAttribute("cv", cvApplication);
        return "applicant/applicant-home";
    }

    @GetMapping("/addCV")
    public String getAddCVPage() {
        return "applicant/cv-fill";
    }

    @PostMapping("/addCV")
    public String addCV(@ModelAttribute @Valid CVApplication cvApplication, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "applicant/applicant-registration";
        }

        var savedCV = userService.saveCV(cvApplication);
        model.addAttribute("cv", savedCV);
        return "redirect:/applicant/applicantHomePage";
    }


    @PostMapping("/findVacancy")
    public String findVacancyByCurrentApplicantCV(Model model) {
        var appropriateVacancies = employeeService.findVacancies(userService.findCVByCurrentUser());
        model.addAttribute("vacancies", appropriateVacancies);
        return "applicant/result-page";
    }

    @PostMapping("/respond/{id}")
    public String respond(@PathVariable String id, Model model) throws OrderExistsException {
        var appropriateVacancies = employeeService.findVacancies(userService.findCVByCurrentUser());
        model.addAttribute("vacancies", appropriateVacancies);
        applicantService.createOrder(id);
        return "applicant/result-page";
    }

}
