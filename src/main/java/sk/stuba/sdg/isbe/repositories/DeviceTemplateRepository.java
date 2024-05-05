package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTemplateRepository extends MongoRepository<Device, String> {

}
