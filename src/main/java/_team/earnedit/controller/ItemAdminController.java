package _team.earnedit.controller;

import _team.earnedit.entity.Item;
import _team.earnedit.entity.Rarity;
import _team.earnedit.entity.Theme;
import _team.earnedit.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class ItemAdminController {

    private final ItemService itemService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "admin/item-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("themes", Theme.values());
        model.addAttribute("rarities", Rarity.values());
        return "admin/item-form";
    }

    @PostMapping
    public String save(@ModelAttribute Item item) {
        itemService.save(item);
        return "redirect:/admin/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        model.addAttribute("themes", Theme.values());
        model.addAttribute("rarities", Rarity.values());
        return "admin/item-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/admin/items";
    }
}
