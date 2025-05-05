package com.OMS.gateway.service;

import reactor.core.publisher.Mono;

public interface gateWayService {
    Mono<String> authenticate(String username, String password);
}
