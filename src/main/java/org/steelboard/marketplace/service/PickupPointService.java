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
        // 1. Создаем и сохраняем адрес
        Address address = new Address();
        address.setCountry("Россия"); // По умолчанию
        address.setCity(dto.getCity().trim());
        address.setStreet(dto.getStreet().trim());
        address.setHouseNumber(dto.getHouseNumber().trim());
        address.setApartmentNumber(dto.getApartmentNumber() != null && !dto.getApartmentNumber().isBlank()
                ? dto.getApartmentNumber().trim() : null);
        address.setPostalCode(dto.getPostalCode().trim());

        addressRepository.save(address);

        // 2. Создаем ПВЗ и привязываем адрес
        PickupPoint pickupPoint = new PickupPoint();
        pickupPoint.setAddress(address);
        pickupPoint.setPhone(dto.getPhone()); // Телефон приходит уже отформатированным с фронта (+7 ...)

        pickupPointRepository.save(pickupPoint);
    }

    // Добавьте dependency: private final OrderRepository orderRepository;

    @Transactional
    public void deletePickupPoint(Long id) {
        // 1. Проверка: есть ли заказы на этом ПВЗ?
        if (orderRepository.existsByPickupPoint_Id(id)) {
            throw new IllegalStateException("Нельзя удалить ПВЗ, так как на него оформлены заказы. Сначала удалите или перенесите заказы.");
        }

        // 2. Находим ПВЗ
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ПВЗ не найден"));

        // 3. Удаляем ПВЗ
        pickupPointRepository.delete(pickupPoint);

        // 4. Удаляем адрес, так как он больше не нужен (если он One-to-One с ПВЗ)
        // Если Address используется где-то еще, эту строчку нужно убрать
        addressRepository.delete(pickupPoint.getAddress());
    }
}
