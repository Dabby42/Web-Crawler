package com.gsd.kolorbi.service;

import com.gsd.kolorbi.model.News;

import java.util.List;

public interface NewsService {
    List<News> getNewsForDeviceId(String deviceId, int page, int size) throws Exception;

    List<News> getRelatedNews(String category, int page, int size) throws Exception;

    void saveNews(News news) throws Exception;

    void clearOldNews() throws Exception;
}
