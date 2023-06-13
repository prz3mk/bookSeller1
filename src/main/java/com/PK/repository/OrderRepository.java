package com.PK.repository;

import com.PK.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> getOrdersByUser(String username);


    List<Orders> findByUser(String user);
}
