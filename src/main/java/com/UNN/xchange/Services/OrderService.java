package com.UNN.xchange.Services;

import com.UNN.xchange.Models.Orders;
import com.UNN.xchange.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Orders saveOrder(Orders order) {
        return orderRepository.save(order);
    }

    public Orders findOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public boolean deleteOrderById(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Orders updateOrder(Long id, Orders updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            order.setBuyer(updatedOrder.getBuyer());
            order.setProducts(updatedOrder.getProducts());
            order.setTotalPrice(updatedOrder.getTotalPrice());
            order.setStatus(updatedOrder.getStatus());
            return orderRepository.save(order);
        }).orElse(null);
    }
}