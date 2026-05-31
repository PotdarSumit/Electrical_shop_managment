package com.ele_shop_mgmt.backend.repository;


import com.ele_shop_mgmt.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

}
