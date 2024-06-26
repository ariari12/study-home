package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItems(Item item){
        itemRepository.save(item);
    }

    public Item findItem(Long id){
        return itemRepository.findOne(id);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }
}
