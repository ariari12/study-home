package com.study.apple.shop.item;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    public void save(Item item, String name) {
        item.setUsername(name);
        itemRepository.save(item);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public void modify(Item item) {
        Item findItem = itemRepository.findById(item.getId()).orElseThrow(IllegalArgumentException::new);
        findItem.setPrice(item.getPrice());
        findItem.setTitle(item.getTitle());
        findItem.setImg(item.getImg());
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public Page<Item> findByPage(Pageable pageable) {
        return itemRepository.findPageBy(pageable);
    }

    public List<Item> findAllByTitleContains(String title) {
        return itemRepository.findAllByTitleContains(title);
    }
}
