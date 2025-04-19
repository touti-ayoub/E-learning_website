package tn.esprit.microservice2.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;
    
    @Column(nullable = false)
    private boolean free = false;

    @Column(nullable = false)
    private Integer durationInMonths;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Coupon> coupons = new HashSet<>();

    // Helper method to validate coupon code for this course
    @Transient
    public Coupon validateCouponCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }

        return coupons.stream()
                .filter(coupon -> coupon.getCode().equalsIgnoreCase(code) && coupon.isActive())
                .findFirst()
                .orElse(null);
    }

    // Method to calculate price with coupon
    @Transient
    public BigDecimal getPriceWithCoupon(String couponCode) {
        Coupon coupon = validateCouponCode(couponCode);
        if (coupon == null) {
            return price;
        }

        return coupon.calculateDiscountedPrice(price);
    }

    // Add getter and setter for coupons
    public Set<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<Coupon> coupons) {
        this.coupons = coupons;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}