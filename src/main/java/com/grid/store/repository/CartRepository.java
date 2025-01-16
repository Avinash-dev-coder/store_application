package com.grid.store.repository;

import com.grid.store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

//    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
//    Optional<Cart> findCartByUserId(@Param("userId") Long userId);

}
