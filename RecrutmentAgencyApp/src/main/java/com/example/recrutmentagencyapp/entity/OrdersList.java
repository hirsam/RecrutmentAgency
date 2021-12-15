package com.example.recrutmentagencyapp.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrdersList {
    private List<Order> orderList;

    public OrdersList() {
        this.orderList = new ArrayList<>();
    }
}
