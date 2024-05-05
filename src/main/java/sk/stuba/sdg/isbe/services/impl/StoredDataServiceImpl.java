package sk.stuba.sdg.isbe.services.impl;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.StoredDataService;

import java.time.Instant;
import java.util.List;

@Service
public class StoredDataServiceImpl implements StoredDataService {

    @Autowired
    private StoredDataRepository storedDataRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public StoredData createStoredData(StoredData storedData){
        if (!storedData.isValid()) {
            throw new InvalidEntityException("Data Point Save has no name or unit set!");
        }

        storedData.setMeasureAt(Instant.now().toEpochMilli());
        return upsertStoredData(storedData);
    }

    @Override
    public List<StoredData> getStoredDatas() {
        return storedDataRepository.findAll();
    }

    @Override
    public StoredData getStoredDataById(String storedDataId) {
        if (storedDataId == null || storedDataId.isEmpty()) {
            throw new InvalidEntityException("Data Point Save id is not set!");
        }

        return storedDataRepository.getStoredDataByUid(storedDataId);
    }

    @Override
    public StoredData updateStoredData(String storedDataId, StoredData changeStoredData) {
        StoredData storedData = getStoredDataById(storedDataId);

        if (changeStoredData == null) {
            throw new InvalidEntityException("StoredData with changes is null!");
        }

        if (changeStoredData.getDataPointTagId() != null) {
            storedData.setDataPointTagId(changeStoredData.getDataPointTagId());
        }

        if (changeStoredData.getTag() != null){
            storedData.setTag(changeStoredData.getTag());
        }

        if (changeStoredData.getValue() != null) {
            storedData.setValue(changeStoredData.getValue());
        }

        return upsertStoredData(storedData);
    }

    @Override
    public StoredData deleteStoredData(String storedDataId) {
        StoredData storedData = getStoredDataById(storedDataId);
        storedData.setDeactivated(true);
        upsertStoredData(storedData);
        return storedData;
    }

    @Override
    public StoredData upsertStoredData(StoredData storedData) {
        Query query = new Query(Criteria.where("uid").is(storedData.getUid()));
        Update update = new Update()
                .set("dataPointTagId", storedData.getDataPointTagId())
                .set("value", storedData.getValue())
                .set("measureAt", storedData.getMeasureAt())
                .set("measureAtDevice", storedData.getMeasureAtDevice())
                .set("deactivated", storedData.isDeactivated())
                .set("deviceId", storedData.getDeviceId())
                .set("tag", storedData.getTag());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, StoredData.class);

        if (updateResult.getMatchedCount() == 0) {
            // if no matching document found, insert a new document
            mongoTemplate.insert(storedData);
        } else {
            // if a matching document is found, update the scenario object with the latest data
            storedData = mongoTemplate.findOne(query, StoredData.class);
        }

        return storedData;
    }
}
