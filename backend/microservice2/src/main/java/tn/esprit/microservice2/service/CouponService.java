package tn.esprit.microservice2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.Model.Coupon;
import tn.esprit.microservice2.Model.Course;
import tn.esprit.microservice2.repo.CouponRepository;
import tn.esprit.microservice2.repo.ICourseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ICourseRepository courseRepository;

    public Coupon createCoupon(Coupon coupon, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        // Validate discount percentage
        if (coupon.getDiscountPercentage() < 1 || coupon.getDiscountPercentage() > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 1 and 100");
        }

        // Check for duplicate code
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists");
        }

        coupon.setCourse(course);
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getCouponsByCourse(Long courseId) {
        return couponRepository.findByCourseId(courseId);
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public BigDecimal applyDiscount(String couponCode, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        Coupon coupon = couponRepository.findByCodeAndCourseId(couponCode, courseId)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code for this course"));

        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon is inactive");
        }

        return coupon.calculateDiscountedPrice(course.getPrice());
    }

    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }
}