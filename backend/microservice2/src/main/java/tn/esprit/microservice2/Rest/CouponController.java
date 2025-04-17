package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.Model.Coupon;
import tn.esprit.microservice2.Model.Course;
import tn.esprit.microservice2.repo.CouponRepository;
import tn.esprit.microservice2.repo.ICourseRepository;
import tn.esprit.microservice2.service.CouponService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mic2/coupons")
public class CouponController {

    private static final LocalDateTime CURRENT_DATETIME =
            LocalDateTime.parse("2025-04-09T16:10:18", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final String CURRENT_USER = "iitsMahdi";

    @Autowired
    private CouponService couponService;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private CouponRepository couponRepository;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon, @PathVariable Long courseId) {
        try {
            Coupon createdCoupon = couponService.createCoupon(coupon, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Coupon>> getCouponsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(couponService.getCouponsByCourse(courseId));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateCoupon(
            @RequestParam String code,
            @RequestParam Long courseId) {
        try {
            Coupon coupon = couponRepository.findByCodeAndCourseId(code, courseId)
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code for this course"));
            BigDecimal discountedPrice = couponService.applyDiscount(code, courseId);
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "discountedPrice", discountedPrice,
                    "code", code,
                    "discountPercentage",coupon.getDiscountPercentage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        }
    }

    @PutMapping("/{couponId}/deactivate")
    public ResponseEntity<?> deactivateCoupon(@PathVariable Long couponId) {
        try {
            couponService.deactivateCoupon(couponId);
            return ResponseEntity.ok(Map.of(
                    "message", "Coupon deactivated successfully",
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        }
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long couponId) {
        try {
            couponService.deleteCoupon(couponId);
            return ResponseEntity.ok(Map.of(
                    "message", "Coupon deleted successfully",
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", CURRENT_DATETIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "username", CURRENT_USER
            ));
        }
    }
}