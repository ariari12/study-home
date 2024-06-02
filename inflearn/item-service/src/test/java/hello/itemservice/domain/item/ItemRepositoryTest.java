package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {
    ItemRepository itemRepository = new ItemRepository();


    @AfterEach //각각의 테스트가 끝날때마다 실행됨
    void afterEach(){
        itemRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Item item = new Item("Item",10000,10);
        //when
        Item savedItem = itemRepository.save(item);
        //then
        Item findItem = itemRepository.findById(savedItem.getId());
        assertThat(findItem).isEqualTo(savedItem);
    }


    @Test
    void findAll() {
        Item item1 = new Item("Item1",10000,10);
        Item item2 = new Item("Item2",20000,20);

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> result = itemRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(item1, item2);
    }

    @Test
    void update() {
        Item item1 = new Item("Item1",10000,10);
        Item updateItem = new Item("Item2", 20000, 20);
        Item saved = itemRepository.save(item1);
        itemRepository.update(saved.getId(), updateItem);

        Item findItem = itemRepository.findById(saved.getId());

        System.out.println();
        assertThat(findItem.getItemName()).isEqualTo(updateItem.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateItem.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateItem.getQuantity());


    }

}