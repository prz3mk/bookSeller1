package com.PK.service;

import com.PK.model.Orders;
import com.PK.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void saveOrder(Orders orders) {
        orderRepository.save(orders);
    }

    public List<Orders> getUserOrders(String username) {
        return orderRepository.getOrdersByUser(username);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }


}
