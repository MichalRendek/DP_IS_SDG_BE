package sk.stuba.sdg.isbe.repositories;

import org.apache.catalina.Store;
import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.util.List;

public interface StoredDataRepository extends MongoRepository<StoredData, String> {
    StoredData getStoredDataByUid(String StoredDataId);

    StoredData findFirstStoredDataByDeviceIdAndTagOrderByMeasureAtDesc(String deviceId, String tag);

    List<StoredData> findAllByMeasureAtBetween(Long measureAt, Long measureAt2);

    List<StoredData> findStoredDataByDataPointTagIdAndMeasureAtBetween(String DataPointTagId,Long measureAt, Long measureAt2);


}
