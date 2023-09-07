package org.plexus.gatewayservice.services;

import lombok.extern.slf4j.Slf4j;
import org.plexus.gatewayservice.constants.Constants;
import org.plexus.gatewayservice.model.User;
import org.plexus.gatewayservice.repository.UserRepository;
import org.plexus.gatewayservice.security.CustomerDetailsService;
import org.plexus.gatewayservice.security.jwt.JWTUtil;
import org.plexus.gatewayservice.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        // log.info("Registro interno de un usuario {}", requestMap);
        try{
            if(validateSingUpMap(requestMap)){
                User user = userRepository.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userRepository.save(getUserFromMap(requestMap));
                    return SecurityUtils.getResponseEntity( "User successfully registered", HttpStatus.CREATED);
                }else{
                    return SecurityUtils.getResponseEntity("The user with this email already exists", HttpStatus.BAD_REQUEST);
                }
            }else{
                return SecurityUtils.getResponseEntity(Constants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return SecurityUtils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("name-email"), requestMap.get("password"))
            );

            if (authentication.isAuthenticated()) {
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>(
                            "{\"token\":\"" +
                                    jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),
                                            customerDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"Message\":\"" + " Wait for administrator approval " + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<String>("{\"Message\":\"" + " Incorrect credentials " + "\"}", HttpStatus.BAD_REQUEST);
    }

    public boolean validateToken(String token) {
        return jwtUtil.valideateToken(token,
                new CustomerDetailsService(userRepository).loadUserByUsername(jwtUtil.extractUsername(token)));
    }

    public boolean isUserAdmin(String token) {
        return "admin".equalsIgnoreCase((String) jwtUtil.extractAllClaims(token).get("role"));
    }

    private boolean validateSingUpMap (Map<String, String> requestMap){
        return requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;
    }

    public void changeUserStatus(long id, String status) {
        if(status.equalsIgnoreCase("active") || status.equalsIgnoreCase("inactive")) {
            User user = userRepository.findById(id).orElseThrow();
            user.setStatus((status.equalsIgnoreCase("active") ? "true" : "false"));
            userRepository.save(user);
        } else {
            throw new RuntimeException(Constants.INVALID_STATUS);
        }
    }

    public void changeUserRole(long id, String role) {
        if(role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("user")) {
            User user = userRepository.findById(id).orElseThrow();
            user.setRole((role.equalsIgnoreCase("admin") ? "admin" : "user"));
            userRepository.save(user);
        } else {
            throw new RuntimeException(Constants.INVALID_ROLE);
        }
    }
}
