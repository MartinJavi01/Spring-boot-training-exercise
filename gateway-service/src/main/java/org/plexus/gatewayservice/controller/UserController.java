package org.plexus.gatewayservice.controller;

import org.plexus.gatewayservice.constants.Constants;
import org.plexus.gatewayservice.repository.UserRepository;
import org.plexus.gatewayservice.security.CustomerDetailsService;
import org.plexus.gatewayservice.security.jwt.JWTUtil;
import org.plexus.gatewayservice.services.UserService;
import org.plexus.gatewayservice.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/validate")
    public ResponseEntity<Boolean> isTokenValid(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(userService.validateToken(token)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<Boolean> isUserAdmin(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(userService.isUserAdmin(token)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) Map<String,String> requestMap){
        try{
            return userService.signUp(requestMap);
        } catch(Exception e){
            e.printStackTrace();
        }

        return SecurityUtils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return SecurityUtils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Object> activateUser(@PathVariable long id, @RequestParam(name = "status") String status,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(userService.validateToken(token) && userService.isUserAdmin(token)) {
            userService.changeUserStatus(id, status);
            return new ResponseEntity<>("User status changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Constants.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/role/{id}")
    public ResponseEntity<Object> changeUserRole(@PathVariable long id, @RequestParam(name = "role") String role,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if(userService.validateToken(token) && userService.isUserAdmin(token)) {
            userService.changeUserRole(id, role);
            return new ResponseEntity<>("User role changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Constants.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }
}