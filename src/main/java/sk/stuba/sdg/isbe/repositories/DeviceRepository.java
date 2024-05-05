package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Device;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    Device findDeviceByMac(String macAddress);

    Optional<Device> getDeviceByUidAndDeactivated(String deviceId, boolean deactivated);

    Optional<Device> getDeviceByUidAndInitApiKey(String deviceId, String deviceKey);

    @Query(value = "{ 'deactivated': ?0 }", fields = "{ 'dataPointTags' : 0 }")
    List<Device> getDevicesByDeactivatedWithoutDataPointTags(boolean deactivated);

    List<Device> getDevicesByDeactivated(boolean deactivated);

    @Query(value = "{ 'user.uid': ?0, 'deactivated': ?1 }", fields = "{ 'dataPointTags' : 0 }")
    List<Device> getDevicesByUserDeactivatedWithoutDataPointTags(String userId, boolean deactivated);

    Device getDeviceByUid(String deviceId);

    @Query(value = "{ 'user.uid': ?0, 'deactivated': ?1 }", fields = "{ 'dataPointTags' : 0 }")
    Page<Device> getDevicesByUserDeactivatedWithoutDataPointTagsPaging(String userId, boolean deactivated, Pageable pageable);
}
