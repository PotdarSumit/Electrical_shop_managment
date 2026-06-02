package com.ele_shop_mgmt.backend.repository;

import com.ele_shop_mgmt.backend.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
