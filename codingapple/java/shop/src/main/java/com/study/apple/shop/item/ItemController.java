package com.study.apple.shop.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @GetMapping("/list")
    public String list(Model model) {
        List<Item> result = itemService.findAll();

        model.addAttribute("items", result);
        return "list";
    }

    @GetMapping("/write")
    public String write(Model model) {
        return "write";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute Item item) {
        itemService.save(item);
        return "redirect:/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "detail";
    }

    @GetMapping("/detail/{id}/modify")
    public String modify(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "modify";
    }

    @PostMapping("/detail/modify")
    public String modify(@ModelAttribute Item item) {
        itemService.modify(item);
        return "redirect:/detail/"+item.getId();
    }
}
