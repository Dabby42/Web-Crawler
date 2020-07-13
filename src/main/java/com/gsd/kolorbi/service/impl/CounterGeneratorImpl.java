package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.Sequence;
import com.gsd.kolorbi.service.CounterGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CounterGeneratorImpl implements CounterGenerator {

    @Autowired
    private MongoOperations mongoOperation;

    @Override
    public long getNextCounter(String type) {
        //get sequence id
        Query query = new Query(Criteria.where("_id").is(type));

        if (mongoOperation.findOne(query, Sequence.class) == null) {
            Sequence newSequence = new Sequence();
            newSequence.setId(type);
            mongoOperation.save(newSequence);
        }

        //increase sequence id by 1
        Update update = new Update();
        update.inc("seq", 1);

        //return new increased id
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        //this is the magic happened.
        Sequence sequence =
                mongoOperation.findAndModify(query, update, options, Sequence.class);

        return sequence.getSeq();
    }

    @Override
    public long getCount(String type) {
        Query query = new Query(Criteria.where("_id").is(type));

        Sequence sequence = mongoOperation.findOne(query, Sequence.class);
        if (sequence != null) {
            return sequence.getSeq();
        }else{
            return 0;
        }
    }
}
