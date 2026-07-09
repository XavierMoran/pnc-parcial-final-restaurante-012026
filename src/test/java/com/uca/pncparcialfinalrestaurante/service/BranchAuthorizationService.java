package com.uca.pncparcialfinalrestaurante.service;

public interface BranchAuthorizationService {

    void validateUserCanManageOrder(Long orderId, String username);
}