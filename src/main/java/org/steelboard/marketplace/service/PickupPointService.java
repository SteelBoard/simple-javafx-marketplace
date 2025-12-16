package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.steelboard.marketplace.entity.PickupPoint;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.PickupPointRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class PickupPointService {

    private PickupPointRepository pickupPointRepository;

    public Page<PickupPoint> findAll(Pageable pageable) {
        return pickupPointRepository.findAll(pageable);
    }

    public void updatePhone(Long id, String phone) {
        PickupPoint point = pickupPointRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        point.setPhone(phone);
        pickupPointRepository.save(point);
    }

    public List<PickupPoint> findAll() {
        return pickupPointRepository.findAll();
    }
}
