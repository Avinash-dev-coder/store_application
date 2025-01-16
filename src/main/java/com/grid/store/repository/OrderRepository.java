package com.grid.store.repository;

import com.grid.store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

//    @Query("SELECT o FROM Orders o WHERE o.user.userId = :userId")
//    Optional<List<Order>> findByUserId(@Param("userId") Long userId);
}
