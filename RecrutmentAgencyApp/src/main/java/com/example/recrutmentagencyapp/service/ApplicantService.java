package com.example.recrutmentagencyapp.service;

import com.example.recrutmentagencyapp.entity.CVApplication;
import com.example.recrutmentagencyapp.entity.Order;
import com.example.recrutmentagencyapp.entity.RecruitmentAgencyUtils;
import com.example.recrutmentagencyapp.entity.User;
import com.example.recrutmentagencyapp.exception.OrderExistsException;
import com.example.recrutmentagencyapp.repository.CVApplicationRepository;
import com.example.recrutmentagencyapp.repository.UserRepository;
import com.example.recrutmentagencyapp.util.OrderApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('APPLICANT')")
public class ApplicantService {
    private CVApplicationRepository cvApplicationRepository;

    private UserRepository userRepository;

    private OrderApiClient orderApiClient;

    @Autowired
    public ApplicantService(OrderApiClient orderApiClient, UserRepository userRepository, CVApplicationRepository cvApplicationRepository) {
        this.orderApiClient = orderApiClient;
        this.userRepository = userRepository;
        this.cvApplicationRepository = cvApplicationRepository;
    }

    public Order createOrder(String vacancyId) throws OrderExistsException {
        if (orderExists(vacancyId)) throw new OrderExistsException("Order already exist");
        var cv = findCVByCurrentUser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();

        var order = com.example.recrutmentagencyapp.entity.Order.builder()
                .salary(cv.getExpectedSalary())
                .dateOfContract(dtf.format(now))
                .CVApplicationId(cv.getId())
                .vacancyId(Long.parseLong(vacancyId))
                .build();

        return orderApiClient.createOrder(order);
    }

    public boolean orderExists(String vacancyId) {
        var cv = findCVByCurrentUser();
        var orders = orderApiClient.getOrders().stream()
                .filter(order -> order.getCVApplicationId().equals(cv.getId()) && order.getVacancyId().equals(Long.parseLong(vacancyId)))
                .collect(Collectors.toList());
    return !orders.isEmpty();
    }

    private User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        return userRepository.findByUsername(username);
    }

    public CVApplication findCVByCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = RecruitmentAgencyUtils.getUsernameFromUserData(principal.toString());

        var user = userRepository.findByUsername(username);
        return cvApplicationRepository.findByUser(user);
    }
}
