package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.DevicePreference;
import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.repository.DevicePreferenceRepository;
import com.gsd.kolorbi.repository.NewsRepository;
import com.gsd.kolorbi.service.NewsService;
import com.mongodb.Block;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mongodb.client.model.Filters.in;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private DevicePreferenceRepository devicePreferenceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    int count = 1;

    @Override
    public List<News> getNewsForDeviceId(String deviceId, int page, int size) throws Exception {
        //get the device news preference
        DevicePreference devicePreference = devicePreferenceRepository.findByDeviceId(deviceId);
        boolean update = false;

        if (devicePreference == null) {
            devicePreference = new DevicePreference();
            devicePreference.setDeviceId(deviceId);
            devicePreference.setCategoryViewCount(new HashMap<>());
            devicePreference.setSourceViewCount(new HashMap<>());
            update = true;
        }

        if (devicePreference.getNewsIds() == null || devicePreference.getNewsIds().isEmpty()) {

            //get the last 1000 news's id,category,source
            /*FindIterable<Document> results = mongoTemplate.getCollection(mongoTemplate.getCollectionName(News.class)).find()
                    .sort(Sorts.descending("crawledDate")).limit(1000)
                    .projection(Projections.include("_id", "category", "source", "crawledDate"));*/
            AggregateIterable<Document> results = mongoTemplate.getCollection(mongoTemplate.getCollectionName(News.class)).aggregate(Arrays.asList(
                    //new Document("$sort", Sorts.descending("crawledDate")),
                    new Document("$sample", new Document("size", 1000)),
                    //Aggregates.addFields(),
                    Aggregates.project(
                            Projections.fields(
                                    Projections.include("_id", "category", "source", "crawledDate")
                            )
                    )));

            Map<String, Long> categoryViewCount = devicePreference.getCategoryViewCount();
            Map<String, Long> sourceViewCount = devicePreference.getSourceViewCount();

            Map<String, Double> viewPreference = new HashMap<>();

            Date now = new Date();
            count = 1;
            results.forEach((Block<? super Document>) document -> {
                String source = document.getString("source");
                String category = document.getString("category");
                Date crawledDate = document.getDate("crawledDate");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(crawledDate);

                double sourceScore = sourceViewCount.get(source.split("\\.")[0].trim()) != null ? sourceViewCount.get(source.split("\\.")[0].trim()) : 0;
                double categoryScore = categoryViewCount.get(category.trim()) != null ? categoryViewCount.get(category) : 0;
                double dateScore = (now.getTime() - crawledDate.getTime()) / (1000 * 60 * 60 * 23);
                double viewScore = 10/ (dateScore + 1);

                if(count % 4 == 0){
                    viewScore = (sourceScore + 1) + (categoryScore + 1) / (dateScore + 1);
                }

                viewPreference.put(document.get("_id").toString(), viewScore);
                count++;
            });

            String[] newsIds = sortPreference(0, viewPreference.size() - 1,
                    viewPreference.keySet().toArray(new String[]{}), viewPreference);


            devicePreference.setNewsIds(Arrays.asList(newsIds));
            update = true;
        }

        if (update) {
            devicePreferenceRepository.save(devicePreference);
        }

        int fromIndex = (page - 1) * size;
        int toIndex = ((page - 1) * size) + (size - 1);
        //System.out.println(fromIndex+"  "+toIndex);

        List<News> news = new ArrayList<>();
        if (fromIndex < devicePreference.getNewsIds().size() && toIndex < devicePreference.getNewsIds().size()) {
            List<String> ids = devicePreference.getNewsIds().subList(fromIndex, toIndex+1);

            String query = "{ id: { $in: [";
            for (int i = 0; i < ids.size(); i++) {
                if (i != 0) {
                    query += ",";
                }
                query += "\"" + ids.get(i).trim() + "\"";
            }
            query += "] } }";

            news = mongoTemplate.find(new BasicQuery(query), News.class);
            news.sort(new Comparator<News>() {
                @Override
                public int compare(News o1, News o2) {
                    return ids.indexOf(o1.getId()) - ids.indexOf(o2.getId());
                }
            });
        }

        return news;
    }

    @Override
    public List<News> getRelatedNews(String category, int page, int size) throws Exception {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createDate").ascending());
        return newsRepository.findByCategory(category, pageRequest);
    }

    @Override
    public void saveNews(News news) throws Exception {
            try {
                newsRepository.save(news);
            }catch(org.springframework.dao.DuplicateKeyException | DuplicateKeyException e){
                News oldNews = newsRepository.findBySourceURL(news.getSourceURL());
                oldNews.setContents(news.getContents());
                newsRepository.save(oldNews);
            }
    }

    @Override
    public void clearOldNews() throws Exception {
        List<String> idsToRemove = new ArrayList<>();
        mongoTemplate.getCollection(mongoTemplate.getCollectionName(News.class)).find()
                .sort(Sorts.descending("crawledDate")).skip(1000)
                .projection(Projections.include("id"))
                .forEach((Block<? super Document>) document -> idsToRemove.add(document.getString("id")));

        if(!idsToRemove.isEmpty()){
            mongoTemplate.getCollection(mongoTemplate.getCollectionName(News.class)).deleteMany(in("id", idsToRemove));
        }
    }

    private String[] sortPreference(int lowerIndex, int higherIndex, String[] array, Map<String, Double> preference) {

        if(higherIndex < 1){
            return array;
        }

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        double pivot = preference.get(array[lowerIndex + (higherIndex - lowerIndex) / 2]);
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (preference.get(array[i]) > pivot) {
                i++;
            }
            while (preference.get(array[j]) < pivot) {
                j--;
            }
            if (i <= j) {
                String temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j) {
            array = sortPreference(lowerIndex, j, array, preference);
        }
        if (i < higherIndex) {
            array = sortPreference(i, higherIndex, array, preference);
        }

        return array;
    }
}
