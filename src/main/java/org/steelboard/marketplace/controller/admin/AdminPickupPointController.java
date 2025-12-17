package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.steelboard.marketplace.entity.PickupPoint;
import org.steelboard.marketplace.repository.PickupPointRepository;
import org.steelboard.marketplace.service.PickupPointService;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pickup-points")
public class AdminPickupPointController {

    private final PickupPointService pickupPointService;
    private final PickupPointRepository pickupPointRepository;

    // Регулярное выражение: +7, пробел, 3 цифры, пробел, 3 цифры, тире, 2 цифры, тире, 2 цифры
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$");

    // --- СПИСОК ПВЗ ---
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapSortField(sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<PickupPoint> pageResult = pickupPointService.findAll(search, pageable);

        model.addAttribute("page", pageResult);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);

        return "admin/pickup_point/pickup_points";
    }

    // --- ДЕТАЛИ ---
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ПВЗ не найден"));
        model.addAttribute("pickupPoint", pickupPoint);
        return "admin/pickup_point/pickup_point_details"; // Убедитесь, что имя файла совпадает
    }

    // --- ОБНОВЛЕНИЕ (С ВАЛИДАЦИЕЙ) ---
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam("phone") String phone,
            Model model // Важно добавить Model для передачи ошибок
    ) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ПВЗ не найден"));

        // 1. Проверяем формат телефона на сервере
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            // Если ошибка:
            model.addAttribute("error", "Неверный формат телефона! Требуется: +7 XXX XXX-XX-XX");

            // Устанавливаем введенный (неверный) телефон в объект, чтобы он отобразился в поле ввода
            // и пользователю не пришлось вводить его заново. В базу НЕ сохраняем.
            pickupPoint.setPhone(phone);
            model.addAttribute("pickupPoint", pickupPoint);

            // Возвращаем ту же страницу HTML (не редирект), чтобы показать ошибку
            return "admin/pickup_point/pickup_point_details";
        }

        // 2. Если все хорошо — сохраняем
        pickupPoint.setPhone(phone);
        pickupPointRepository.save(pickupPoint);

        // Редирект с параметром success
        return "redirect:/admin/pickup-points/" + id + "?success";
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "city" -> "address.city";
            case "street" -> "address.street";
            case "phone" -> "phone";
            default -> "id";
        };
    }
}