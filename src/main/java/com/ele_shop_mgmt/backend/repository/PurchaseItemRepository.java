package com.ele_shop_mgmt.backend.repository;

import com.ele_shop_mgmt.backend.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItemRepository  extends JpaRepository<PurchaseItem, Long> {

}
