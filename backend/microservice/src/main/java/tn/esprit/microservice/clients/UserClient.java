// File: backend/microservice/src/main/java/tn/esprit/microservice/clients/UserClient.java
package tn.esprit.microservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.microservice.dto.UserDTO;


@FeignClient(name = "user-microservice")
public interface UserClient {

    @GetMapping("/auth/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/auth/username/{email}")
    UserDTO getUserByUsername(@PathVariable("email") String email);
}

