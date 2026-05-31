package com.ele_shop_mgmt.backend.service.impl;


import com.ele_shop_mgmt.backend.entity.User;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.UserRepository;
/*import com.ele_shop_mgmt.backend.service.interfaces.userDetailService;*/
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole().name())))
                .disabled(!user.isEnabled())
                .build();
    }
}
