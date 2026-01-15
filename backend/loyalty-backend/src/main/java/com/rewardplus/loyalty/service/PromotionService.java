package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.model.Promotion;
import com.rewardplus.loyalty.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    // Create a new promotion
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    // Retrieve all promotions
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    // Retrieve a promotion by ID
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id).orElse(null);
    }

    // Update an existing promotion
    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion != null) {
            promotion.setName(promotionDetails.getName());
            promotion.setDescription(promotionDetails.getDescription());
            promotion.setStartDate(promotionDetails.getStartDate());
            promotion.setEndDate(promotionDetails.getEndDate());
            return promotionRepository.save(promotion);
        }
        return null;
    }

    // Delete a promotion
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
}