package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.Collection;

public interface CollectionRepository extends MongoRepository<Collection, String> {

}
