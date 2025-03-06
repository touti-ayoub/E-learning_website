package tn.esprit.microservice2.DTO;

import lombok.Data;
import lombok.Getter;
import tn.esprit.microservice2.Model.SubscriptionPlan;

@Data
@Getter
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int durationMonths;

    public static SubscriptionPlanDTO fromEntity(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setDurationMonths(plan.getDurationMonths());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }
}