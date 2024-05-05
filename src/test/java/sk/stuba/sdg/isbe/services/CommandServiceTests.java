package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("!deployment")
public class CommandServiceTests {

    @Autowired
    private CommandService commandService;

    @Autowired
    private CommandRepository commandRepository;

    private static final String NONE = "NONE";

    @Test
    void testCreateCommand() {
        Exception exception = assertThrows(InvalidEntityException.class, () -> commandService.createCommand(new Command()));
        assertEquals("Name of the command is not valid!", exception.getMessage());

        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command);
        exception = assertThrows(EntityExistsException.class, () -> commandService.createCommand(command));
        assertEquals("Command with name: '" + command.getName() + "' already exists!", exception.getMessage());

        command.setParams(null);
        command.setName(command.getName() + "1");
        exception = assertThrows(InvalidEntityException.class, () -> commandService.createCommand(command));
        assertEquals("Command does not contain any parameters!", exception.getMessage());

        commandRepository.delete(command);
    }

    @Test
    void testUpdateCommand() {
        Command existingCommand = new Command("existingCommand" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(existingCommand);
        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command);

        Exception exception = assertThrows(InvalidEntityException.class, () -> commandService.updateCommand(command.getId(), null));
        assertEquals("Command with changes is null!", exception.getMessage());

        exception = assertThrows(EntityExistsException.class, () -> commandService.updateCommand(command.getId(), existingCommand));
        assertEquals("Command with name: '" + existingCommand.getName() + "' already exists!", exception.getMessage());

        commandRepository.delete(command);
        commandRepository.delete(existingCommand);
    }


    @Test
    void testGetCommand() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        command.setDeactivated(true);
        commandService.createCommand(command);
        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.getCommandById(command.getId()));
        assertEquals("Command with ID: '" + command.getId() + "' was not found!", exception.getMessage());

        command.setDeactivated(false);
        commandService.createCommand(command);
        commandService.getCommandById(command.getId());
        commandRepository.delete(command);
    }

    @Test
    void testGetCommandsByDeviceType() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command);
        Command command2 = new Command("command2 " + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.SDG_CUBE, false);
        commandService.createCommand(command2);

        List<Command> espCommands = commandService.getCommandsByDeviceType(DeviceTypeEnum.ESP32.name(), NONE, NONE);
        assertFalse(espCommands.isEmpty());
        assertTrue(espCommands.stream().allMatch(c -> c.getDeviceType() == DeviceTypeEnum.ESP32));

        List<Command> sdgCommands = commandService.getCommandsByDeviceType(DeviceTypeEnum.SDG_CUBE.name(), NONE, NONE);
        assertFalse(sdgCommands.isEmpty());
        assertTrue(sdgCommands.stream().allMatch(c -> c.getDeviceType() == DeviceTypeEnum.SDG_CUBE));

        commandRepository.delete(command);
        commandRepository.delete(command2);
    }

    @Test
    void testGetCommandsByDeviceTypePageable() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command);
        Command command2 = new Command("command2 " + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command2);

        List<Command> commands;
        commands = commandService.getCommandsByDeviceTypePageable(DeviceTypeEnum.ESP32.name(), 1, 2, NONE, NONE);
        assertEquals(2, commands.size());

        commands = commandService.getCommandsByDeviceTypePageable(DeviceTypeEnum.ESP32.name(), 1, 1, NONE, NONE);
        assertEquals(1, commands.size());

        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.getCommandsByDeviceTypePageable(DeviceTypeEnum.ESP32.name(), 10000000, 1, NONE, NONE));
        assertEquals("There are not any commands with type of device: '" + DeviceTypeEnum.ESP32.name() + "' on page " + 10000000 + "!", exception.getMessage());

        commandRepository.delete(command);
        commandRepository.delete(command2);
    }

}
