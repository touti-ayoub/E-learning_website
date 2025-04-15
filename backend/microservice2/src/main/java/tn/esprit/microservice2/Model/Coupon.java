package tn.esprit.microservice2.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private int discountPercentage;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    // Calculate discounted price
    @Transient
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        if (!isActive) {
            return originalPrice;
        }

        BigDecimal discount = originalPrice
                .multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

        return originalPrice.subtract(discount);
    }

    // Check if coupon is valid
    @Transient
    public boolean isValid() {
        return isActive;
    }
}