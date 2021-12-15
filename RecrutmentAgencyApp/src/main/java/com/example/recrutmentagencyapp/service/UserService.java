package com.example.recrutmentagencyapp.service;

import com.example.recrutmentagencyapp.entity.CVApplication;
import com.example.recrutmentagencyapp.entity.Order;
import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.entity.Report;
import com.example.recrutmentagencyapp.entity.Role;
import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.repository.CVApplicationRepository;
import com.example.recrutmentagencyapp.repository.UserRepository;
import com.example.recrutmentagencyapp.util.OrderApiClient;
import com.example.recrutmentagencyapp.util.ReportApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@EnableAutoConfiguration
@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private CVApplicationRepository cvApplicationRepository;
    private OrderApiClient orderApiClient;
    private BCryptPasswordEncoder passwordEncoder;
    private ReportApiClient reportApiClient;

    @Autowired
    public UserService(UserRepository userRepository, CVApplicationRepository cvApplicationRepository,
                       BCryptPasswordEncoder passwordEncoder, OrderApiClient orderApiClient, ReportApiClient reportApiClient) {
        this.userRepository = userRepository;
        this.cvApplicationRepository = cvApplicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderApiClient = orderApiClient;
        this.reportApiClient = reportApiClient;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public CVApplication saveCV(CVApplication cvApplication) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        var currentUser = userRepository.findByUsername(username);

        if(cvApplicationRepository.existsByUser(currentUser)) cvApplicationRepository.deleteAllByUser(currentUser);
        currentUser.setCvApplication(cvApplication);
        cvApplication.setUser(currentUser);
        cvApplicationRepository.save(cvApplication);
        userRepository.save(currentUser);

        return cvApplication;
    }

    public CVApplication findCVByCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        var user = userRepository.findByUsername(username);
        return cvApplicationRepository.findByUser(user);
    }

    public boolean saveUser(User user, String role) {
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.valueOf(role)));
        userRepository.save(user);
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Report createReport() {
        var reportForLastMonth = generateReportForLastMonth();
        return reportApiClient.createReport(reportForLastMonth);
    }

    private Report generateReportForLastMonth() {
        LocalDateTime now = LocalDateTime.now();
        var ordersForLastMonth = filterOrdersByCurrentMonth(orderApiClient.getOrders());
        var employedNumber = getEmployedClientsAmount(ordersForLastMonth);
        var averageSalary =  getAverageSalary(ordersForLastMonth);
        var reportForLastMonth = new Report();
        reportForLastMonth.setNumberOfEmployed(employedNumber);
        reportForLastMonth.setAverageSalary(averageSalary);
        return reportForLastMonth;
    }

    private Integer getEmployedClientsAmount(List<Order> orders) {
        return orders.stream()
                .map(Order::getSalary)
                .collect(Collectors.toSet())
                .size();
    }


    private Integer getAverageSalary(List<Order> orders) {
        return (orders.stream()
                .map(Order::getSalary)
                .reduce(0, Integer::sum)) / orders.size() != 0? orders.size(): 1;
    }

    private List<Order> filterOrdersByCurrentMonth(List<Order> orders) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String now = dtf.format(LocalDateTime.now());
        String currentMonth = now.charAt(3) + "" + now.charAt(4);
        return orders.stream()
                .filter(order -> {
                    String monthOfContract = order.getDateOfContract().charAt(3) + "" +
                            order.getDateOfContract().charAt(3);
                    return monthOfContract.equals(currentMonth);
                })
                .collect(Collectors.toList());
    }
}
