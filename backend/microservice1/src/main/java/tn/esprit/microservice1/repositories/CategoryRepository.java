// CategoryRepository.java
package tn.esprit.microservice1.repositories;

import tn.esprit.microservice1.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}