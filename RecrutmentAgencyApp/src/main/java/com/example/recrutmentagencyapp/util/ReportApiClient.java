package com.example.recrutmentagencyapp.util;

import com.example.recrutmentagencyapp.entity.Order;
import com.example.recrutmentagencyapp.entity.Report;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ReportApiClient {

    private RestTemplate restTemplate;

    private HttpHeaders httpHeaders;

    private String baseUrl;

    private ObjectMapper objectMapper;

    public ReportApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "http://localhost:8082/reports";
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public Report createReport(Report report) {
        String response = null;
        Report result = null;
        try {
            HttpEntity<String> request =
                    new HttpEntity<>(objectMapper.writeValueAsString(report), httpHeaders);
            response = restTemplate.postForObject(baseUrl, request, String.class);
            result = objectMapper.readValue(response, Report.class);
        }
        catch (Exception e) {
        }
        return result;
    }

    public List<Report> getReports() {
        ResponseEntity<Report[]> response =
                restTemplate.getForEntity(
                        baseUrl,
                        Report[].class);
        Report[] orders = response.getBody();
        assert orders != null;
        return Arrays.asList(orders);
    }
}
