package tn.esprit.microservice4.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.microservice4.dto.UserDTO;

import java.util.List;

@FeignClient(name = "user-microservice")
public interface UserClient {

    @GetMapping("/auth/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/auth/username/{email}")
    UserDTO getUserByUsername(@PathVariable("email") String email);

    @GetMapping("/auth/all")
    List<UserDTO> getAllUsers();
}