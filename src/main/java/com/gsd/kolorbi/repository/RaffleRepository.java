package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.Raffle;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaffleRepository extends PagingAndSortingRepository<Raffle, String> {
}
