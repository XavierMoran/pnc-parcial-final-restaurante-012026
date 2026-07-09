package com.uca.pncparcialfinalrestaurante.service.impl;

import com.uca.pncparcialfinalrestaurante.entity.Order;
import com.uca.pncparcialfinalrestaurante.entity.User;
import com.uca.pncparcialfinalrestaurante.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.exception.BusinessRuleException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.OrderRepository;
import com.uca.pncparcialfinalrestaurante.repository.UserRepository;
import com.uca.pncparcialfinalrestaurante.service.BranchAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchAuthorizationServiceImpl implements BranchAuthorizationService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public void validateUserCanManageOrder(Long orderId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        RoleName role = user.getRole().getName();

        if (role == RoleName.ADMIN) {
            return;
        }

        if (role == RoleName.MANAGER) {
            Long userRestaurantId = user.getRestaurant().getId();
            Long orderRestaurantId = order.getTable().getRestaurant().getId();

            if (!userRestaurantId.equals(orderRestaurantId)) {
                throw new BusinessRuleException("El encargado solo puede gestionar pedidos de su propia sucursal");
            }

            return;
        }

        throw new BusinessRuleException("No tiene permisos para gestionar este pedido");
    }
}