package com.example.recrutmentagencyapp.util;

import com.example.recrutmentagencyapp.entity.Order;
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
public class OrderApiClient {
    private RestTemplate restTemplate;

    private HttpHeaders httpHeaders;

    private String baseUrl;

    private ObjectMapper objectMapper;

    public OrderApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "http://localhost:8081/orders";
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public Order createOrder(Order order) {
        String response = null;
        Order result = null;
        try {
            HttpEntity<String> request =
                    new HttpEntity<>(objectMapper.writeValueAsString(order), httpHeaders);
            response = restTemplate.postForObject(baseUrl, request, String.class);
            result = objectMapper.readValue(response, Order.class);
        }
        catch (Exception e) {
        }
        return result;
    }

    public List<Order> getOrders() {
        ResponseEntity<Order[]> response =
                restTemplate.getForEntity(
                        baseUrl,
                        Order[].class);
        Order[] orders = response.getBody();
        return Arrays.asList(orders);
    }


}
