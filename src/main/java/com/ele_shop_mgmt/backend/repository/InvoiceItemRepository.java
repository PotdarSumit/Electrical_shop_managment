package com.ele_shop_mgmt.backend.repository;

import com.ele_shop_mgmt.backend.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

}
