package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

    List<Job> getJobsByName(String name);

    List<Job> getJobsByDeviceIdAndCurrentStatusIs(String deviceId, JobStatusEnum currentStatus, Sort sort);

    List<Job> getJobsByDeviceIdAndCurrentStatusIs(String deviceId, JobStatusEnum currentStatus, Pageable pageable);

    List<Job> getJobsByDeviceId(String deviceId, Sort sort);

    List<Job> getJobsByDeviceId(String deviceId, Pageable pageable);
}
