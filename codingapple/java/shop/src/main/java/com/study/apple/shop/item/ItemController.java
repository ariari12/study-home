package com.study.apple.shop.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public String addPost(@ModelAttribute Item item, Authentication auth) {
        itemService.save(item, auth.getName());
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

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestBody Long id) {
        itemService.deleteById(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

    @GetMapping("/test2")
    public String deleteItem() {
        String encode = new BCryptPasswordEncoder().encode("문자~~~~");
        System.out.println(encode);
        return "redirect:/list";
    }

    @GetMapping("/list/{page}")
    public String getListPage(Model model, @PathVariable Integer page) {
        Page<Item> result = itemService.findByPage(PageRequest.of(page, 5));


        model.addAttribute("items", result);
        model.addAttribute("totalPage", result.getTotalPages()-1);
        return "list";
    }
}
