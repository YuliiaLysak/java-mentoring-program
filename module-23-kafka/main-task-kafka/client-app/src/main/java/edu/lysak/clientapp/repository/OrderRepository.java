package edu.lysak.clientapp.repository;

import edu.lysak.clientapp.domain.Order;
import edu.lysak.domain.models.Status;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o.status FROM Order o WHERE o.orderId = :orderId")
    Optional<Status> findOrderStatusById(@Param("orderId") Long orderId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderId = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Status status);
}
