package tn.esprit.microservice2.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice2.Model.Coupon;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    boolean existsByCode(String code);

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByCourseId(Long courseId);

    Optional<Coupon> findByCodeAndCourseId(String code, Long courseId);

    List<Coupon> findByIsActiveTrue();
}
