package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.RafflePlayed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RafflePlayedRepository extends PagingAndSortingRepository<RafflePlayed, String> {

    @Query("{ 'raffleId' : ?0, active : true }")
    List<RafflePlayed> findActiveRafflePlayedRaffleId(String raffleId) throws Exception;

    @Query("{ 'winCode' : ?0}")
    RafflePlayed findRafflePlayedByWinCode(String winCode) throws Exception;

    @Query("{'createDate' : { '$lt' : ?0 }, 'raffleId' : ?1, active : true}")
    List<RafflePlayed> findRafflePlayedsDueForDraw(Date date, String RaffleId);
}
