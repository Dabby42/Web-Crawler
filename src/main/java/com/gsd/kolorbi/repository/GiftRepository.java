package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.Gift;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository  extends PagingAndSortingRepository<Gift, String> {

    @Query("{ 'winCode' : ?0}")
    Gift findGiftByWinCode(String winCode) throws Exception;
}
