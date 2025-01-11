package com.study.apple.shop.sales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesRepository extends JpaRepository<Sales, Long> {


    @Query("select s from Sales s join s.member")
    Page<Sales> findPageSales(Pageable pageable);
}
