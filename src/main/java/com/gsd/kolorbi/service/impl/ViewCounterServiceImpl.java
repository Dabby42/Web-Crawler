package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.DevicePreference;
import com.gsd.kolorbi.service.ViewCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ViewCounterServiceImpl implements ViewCounterService {

    @Qualifier("threadPoolTaskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MongoOperations mongoOperation;


    @Override
    public void count(String deviceId, String source, String category) {
        if(deviceId == null || deviceId.isEmpty()){
            return;
        }

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //get sequence id
                Query query = new Query(Criteria.where("deviceId").is(deviceId));
                Query queryNG = new Query(Criteria.where("deviceId").is("NG"));

                //increase sourceCount by 1
                Update update = new Update();
                update.inc("sourceViewCount."+source.split("\\.")[0].trim(), 1);
                update.inc("categoryViewCount."+category.trim(), 1);

                FindAndModifyOptions options = new FindAndModifyOptions();
                options.returnNew(false);

                //this is the magic happened.
                mongoOperation.findAndModify(query, update, options, DevicePreference.class);
                mongoOperation.findAndModify(queryNG, update, options, DevicePreference.class);
            }
        });
    }
}
