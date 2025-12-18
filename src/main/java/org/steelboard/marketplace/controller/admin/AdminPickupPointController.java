package org.steelboard.marketplace.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.dto.PickupPointAddDto;
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

    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$");

    
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
        model.addAttribute("activeTab", "pvz");

        return "admin/pickup_point/pickup_points";
    }

    
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ПВЗ не найден"));
        model.addAttribute("pickupPoint", pickupPoint);
        return "admin/pickup_point/pickup_point_details"; 
    }

    
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam("phone") String phone,
            Model model 
    ) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ПВЗ не найден"));

        
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            
            model.addAttribute("error", "Неверный формат телефона! Требуется: +7 XXX XXX-XX-XX");

            
            
            pickupPoint.setPhone(phone);
            model.addAttribute("pickupPoint", pickupPoint);

            
            return "admin/pickup_point/pickup_point_details";
        }

        
        pickupPoint.setPhone(phone);
        pickupPointRepository.save(pickupPoint);

        
        return "redirect:/admin/pickup-points/" + id + "?success";
    }

    @GetMapping("/new")
    public String newPickupPointPage(Model model) {
        if (!model.containsAttribute("dto")) {
            model.addAttribute("dto", new PickupPointAddDto());
        }
        model.addAttribute("activeTab", "pvz"); // Подсветка меню
        return "admin/pickup_point/add_pickup_point";
    }

    // 2. Обработка формы (POST)
    @PostMapping("/new")
    public String createPickupPoint(
            @Valid @ModelAttribute("dto") PickupPointAddDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "pvz");
            return "admin/pickup_point/add_pickup_point"; // Возвращаем форму с ошибками
        }

        pickupPointService.createPickupPoint(dto);
        return "redirect:/admin/pickup-points?success";
    }

    @PostMapping("/{id}/delete")
    public String deletePickupPoint(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pickupPointService.deletePickupPoint(id);
            redirectAttributes.addFlashAttribute("success", "Пункт выдачи успешно удален.");
            return "redirect:/admin/pickup-points";
        } catch (IllegalStateException e) {
            // Если есть заказы — возвращаем ошибку на страницу редактирования этого ПВЗ
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/pickup-points/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при удалении.");
            return "redirect:/admin/pickup-points/" + id;
        }
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