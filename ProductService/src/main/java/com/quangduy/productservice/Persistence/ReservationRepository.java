package com.quangduy.productservice.Persistence;


import com.quangduy.productservice.Business.Domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationId(Long reservationId);
}
