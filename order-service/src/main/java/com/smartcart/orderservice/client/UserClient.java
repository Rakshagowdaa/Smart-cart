package com.smartcart.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @PutMapping("/api/users/{id}/new-user")
    void updateNewUserFlag(@PathVariable("id") Long id, @RequestParam("isNewUser") boolean isNewUser);

    class UserDto {
        private Long id;
        private boolean isNewUser;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public boolean isNewUser() { return isNewUser; }
        public void setNewUser(boolean newUser) { isNewUser = newUser; }
    }
}
