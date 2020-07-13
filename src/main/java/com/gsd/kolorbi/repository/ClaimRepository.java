package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.Claim;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends PagingAndSortingRepository<Claim, String> {
}
