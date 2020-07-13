package com.gsd.kolorbi.repository;

import com.gsd.kolorbi.model.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<News, String> {

    @Query("{ 'sourceURL' : ?0 }")
    News findBySourceURL(String sourceURL) throws Exception;

    @Query("{ 'category' : ?0 }")
    List<News> findByCategory(String category, Pageable pageable) throws Exception;
}
