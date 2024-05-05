package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.LastStoredData;

public interface LastStoredDataRepository extends MongoRepository<LastStoredData, String> {
    LastStoredData findByDeviceIdAndTag(String deviceId, String tag);
}
