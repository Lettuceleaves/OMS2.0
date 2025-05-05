package com.OMS.auth.service;

import com.OMS.auth.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface authService {

    user register(String username, String password);

    user authenticate(String username, String password);

}
