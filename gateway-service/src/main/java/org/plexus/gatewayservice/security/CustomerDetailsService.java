package org.plexus.gatewayservice.security;

import lombok.extern.slf4j.Slf4j;
import org.plexus.gatewayservice.model.User;;
import org.plexus.gatewayservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    private User userDetail;

    public CustomerDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.contains("@")) {
            userDetail = userRepository.findByEmail(username);
        } else {
            userDetail = userRepository.findByName(username);
        }

        if(!Objects.isNull(userDetail)) {
            if(username.contains("@")) {
                return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
            } else {
                return new org.springframework.security.core.userdetails.User(userDetail.getName(), userDetail.getPassword(), new ArrayList<>());
            }
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public User getUserDetail() {
        return userDetail;
    }
}
