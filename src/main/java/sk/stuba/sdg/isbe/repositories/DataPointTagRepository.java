package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;

public interface DataPointTagRepository extends MongoRepository<DataPointTag, String> {

    DataPointTag getDataPointTagByUid(String dataPointTagId);
}
