package _team.earnedit.controller.admin;

import _team.earnedit.dto.item.ItemRequest;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Rarity;
import _team.earnedit.entity.Theme;
import _team.earnedit.service.admin.item.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("item", new ItemRequest());
        model.addAttribute("themes", Theme.values());
        model.addAttribute("rarities", Rarity.values());
        model.addAttribute("action", "/admin/items");
        return "admin/item-form";
    }

    @PostMapping
    public String save(@ModelAttribute("item") @Valid ItemRequest item,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("themes", Theme.values());
            model.addAttribute("rarities", Rarity.values());
            model.addAttribute("action", "/admin/items");
            return "admin/item-form";
        }

        itemService.save(item.toEntity(item));
        return "redirect:/admin/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", ItemRequest.from(item));
        model.addAttribute("themes", Theme.values());
        model.addAttribute("rarities", Rarity.values());
        model.addAttribute("action", "/admin/items/" + id + "/edit");
        return "admin/item-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("item") @Valid ItemRequest item,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("themes", Theme.values());
            model.addAttribute("rarities", Rarity.values());
            model.addAttribute("action", "/admin/items/" + id + "/edit");
            return "admin/item-form";
        }

        itemService.update(id, item);
        return "redirect:/admin/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/admin/items";
    }
}
