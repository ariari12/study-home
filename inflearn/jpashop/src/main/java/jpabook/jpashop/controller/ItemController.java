package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setIsbn(form.getIsbn());
        book.setAuthor(form.getAuthor());
        book.setStockQuantity(form.getStockQuantity());
        log.info("======================={}",book.getId());
        itemService.saveItems(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }
    @GetMapping("items/{itemId}/edit")
    public String list(@PathVariable("itemId") Long itemId, Model model){
        Book form = new Book();
        Book item = (Book) itemService.findItem(itemId);
        form.setId(item.getId());
        form.setStockQuantity(item.getStockQuantity());
        form.setCategories(item.getCategories());
        form.setName(item.getName());
        form.setIsbn(item.getIsbn());
        form.setAuthor(item.getAuthor());
        form.setPrice(item.getPrice());


        model.addAttribute("form",form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form){
        Book book = new Book();
        book.setId(form.getId());
        book.setStockQuantity(form.getStockQuantity());
        book.setName(form.getName());
        book.setIsbn(form.getIsbn());
        book.setAuthor(form.getAuthor());
        book.setPrice(form.getPrice());
        itemService.saveItems(book);
        return "redirect:/items";
    }
}
