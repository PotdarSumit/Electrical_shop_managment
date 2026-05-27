package com.ele_shop_mgmt.backend.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface userDetailService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
