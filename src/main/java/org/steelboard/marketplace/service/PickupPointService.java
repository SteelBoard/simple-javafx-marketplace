package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.dto.PickupPointAddDto;
import org.steelboard.marketplace.entity.Address;
import org.steelboard.marketplace.entity.PickupPoint;
import org.steelboard.marketplace.repository.AddressRepository;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class PickupPointService {

    private PickupPointRepository pickupPointRepository;
    private AddressRepository addressRepository;
    private OrderRepository orderRepository;

    public Page<PickupPoint> findAll(Pageable pageable) {
        return pickupPointRepository.findAll(pageable);
    }

    public List<PickupPoint> findAll() {
        return pickupPointRepository.findAll();
    }

    public Page<PickupPoint> findAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return pickupPointRepository.searchByCityOrStreetOrPhone(search.trim(), pageable);
        }
        return pickupPointRepository.findAll(pageable);
    }

    @Transactional
    public void createPickupPoint(PickupPointAddDto dto) {
        
        Address address = new Address();
        address.setCountry("Россия"); 
        address.setCity(dto.getCity().trim());
        address.setStreet(dto.getStreet().trim());
        address.setHouseNumber(dto.getHouseNumber().trim());
        address.setApartmentNumber(dto.getApartmentNumber() != null && !dto.getApartmentNumber().isBlank()
                ? dto.getApartmentNumber().trim() : null);
        address.setPostalCode(dto.getPostalCode().trim());

        addressRepository.save(address);

        
        PickupPoint pickupPoint = new PickupPoint();
        pickupPoint.setAddress(address);
        pickupPoint.setPhone(dto.getPhone()); 

        pickupPointRepository.save(pickupPoint);
    }

    

    @Transactional
    public void deletePickupPoint(Long id) {
        
        if (orderRepository.existsByPickupPoint_Id(id)) {
            throw new IllegalStateException("Нельзя удалить ПВЗ, так как на него оформлены заказы. Сначала удалите или перенесите заказы.");
        }

        
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ПВЗ не найден"));

        
        pickupPointRepository.delete(pickupPoint);

        
        
        addressRepository.delete(pickupPoint.getAddress());
    }
}
