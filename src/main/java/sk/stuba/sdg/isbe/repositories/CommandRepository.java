package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {

    Optional<Command> getCommandByIdAndDeactivated(String id, boolean deactivated);

    List<Command> getCommandsByDeactivated(boolean deactivated, Sort sort);

    List<Command> getCommandsByDeactivated(boolean deactivated, Pageable pageable);

    List<Command> getCommandsBySubCommandsContainingAndDeactivated(Command recipe, boolean deactivated);

    List<Command> getCommandsByDeviceTypeAndDeactivated(DeviceTypeEnum deviceType, boolean deactivated, Sort sort);

    List<Command> getCommandsByDeviceTypeAndDeactivated(DeviceTypeEnum deviceType, boolean deactivated, Pageable pageable);
}
