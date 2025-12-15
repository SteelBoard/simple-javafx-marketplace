package org.steelboard.marketplace.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.meta.AdminMetaRegistry;
import org.steelboard.marketplace.service.AdminCrudService;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDbController {

    private final AdminCrudService service;
    private final AdminMetaRegistry registry;

    public AdminDbController(AdminCrudService service, AdminMetaRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @GetMapping
    public String tabs(@RequestParam(required = false) String table, Model model) {

        String active = (table != null)
                ? table
                : registry.tables().iterator().next();

        model.addAttribute("tables", registry.tables());
        model.addAttribute("activeTable", active);
        model.addAttribute("rows", service.findAllAsMaps(active));
        model.addAttribute("meta", registry.get(active));

        return "admin/tabs";
    }


    @GetMapping("/{table}/{id}")
    public String view(@PathVariable String table,
                       @PathVariable Long id,
                       Model model) {

        model.addAttribute("table", table);
        model.addAttribute("id", id);
        model.addAttribute("fields", service.extractFields(service.findById(table, id)));
        model.addAttribute("meta", registry.get(table));

        return "admin/view";
    }

    @GetMapping("/{table}/{id}/edit")
    public String edit(@PathVariable String table,
                       @PathVariable Long id,
                       Model model) {

        model.addAttribute("table", table);
        model.addAttribute("id", id);
        model.addAttribute("fields", service.extractFields(service.findById(table, id)));
        model.addAttribute("meta", registry.get(table));

        return "admin/edit";
    }

    @PostMapping("/{table}/{id}/edit")
    public String update(@PathVariable String table,
                         @PathVariable Long id,
                         @RequestParam Map<String, String> params) {

        params.remove("_csrf");
        service.update(table, id, params);

        return "redirect:/admin/" + table + "/" + id;
    }
}
