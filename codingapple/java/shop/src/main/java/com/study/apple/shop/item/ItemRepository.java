package com.study.apple.shop.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findPageBy(Pageable page);

    Slice<Item> findSliceBy(Pageable page); // Page 랑 다르게 전체 행개수 쿼리가 안날라감

    @Query(value = "select * from item where match(title) against(?1)", nativeQuery = true)
    List<Item> findAllByTitleContains(String title);
}
