package tn.esprit.microservice2.comm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.microservice2.DTO.UserDTO;

@FeignClient(name = "user-microservice", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/auth/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/auth/username/{email}")
    UserDTO getUserByUsername(@PathVariable("email") String email);
}