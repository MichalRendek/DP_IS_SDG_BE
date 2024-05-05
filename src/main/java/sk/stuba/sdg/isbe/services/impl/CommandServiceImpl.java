package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.utilities.DeviceTypeUtils;
import sk.stuba.sdg.isbe.utilities.SortingUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandRepository commandRepository;

    @Override
    public Command createCommand(Command command) {
        if (command.getName() == null || command.getName().isEmpty()) {
            throw new InvalidEntityException("Name of the command is not valid!");
        }
        if (command.getDeviceType() == null) {
            throw new InvalidEntityException("Type of device for command must be set!");
        }
        command.setCreatedAt(Instant.now().toEpochMilli());

        return commandRepository.save(command);
    }

    @Override
    public Command getCommandById(String commandId) {
        Optional<Command> optionalCommand = commandRepository.getCommandByIdAndDeactivated(commandId, false);
        if (optionalCommand.isEmpty()) {
            throw new NotFoundCustomException("Command with ID: '" + commandId + "' was not found!");
        }
        return optionalCommand.get();
    }

    @Override
    public List<Command> getAllCommands(String sortBy, String sortDirection) {
        List<Command> commands = commandRepository.getCommandsByDeactivated(false, SortingUtils.getSort(Command.class, sortBy, sortDirection));
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database!");
        }
        return commands;
    }

    @Override
    public List<Command> getAllCommandsPageable(int page, int pageSize, String sortBy, String sortDirection) {
        Pageable pageable = SortingUtils.getFirstEntry(Command.class);
        List<Command> commands = commandRepository.getCommandsByDeactivated(false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database yet!");
        }

        pageable = SortingUtils.getPagination(Command.class, sortBy, sortDirection, page, pageSize);
        commands = commandRepository.getCommandsByDeactivated(false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands on page " + page + "!");
        }
        return commands;
    }

    @Override
    public List<Command> getCommandsByDeviceType(String deviceType, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        List<Command> commands = commandRepository.getCommandsByDeviceTypeAndDeactivated(deviceTypeEnum, false, SortingUtils.getSort(Command.class, sortBy, sortDirection));
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands with this type of device in the database!");
        }
        return commands;
    }

    @Override
    public List<Command> getCommandsByDeviceTypePageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        Pageable pageable = SortingUtils.getFirstEntry(Command.class);
        List<Command> commands = commandRepository.getCommandsByDeviceTypeAndDeactivated(deviceTypeEnum, false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database of device type: " + deviceType + "!");
        }

        pageable = SortingUtils.getPagination(Command.class, sortBy, sortDirection, page, pageSize);
        commands = commandRepository.getCommandsByDeviceTypeAndDeactivated(deviceTypeEnum, false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands with type of device: '" + deviceType + "' on page " + page + "!");
        }
        return commands;
    }

    @Override
    public Command deleteCommand(String commandId) {
        Command command = getCommandById(commandId);

        if (checkIfCommandUsedAsSubCommand(command)) {
            throw new InvalidOperationException("Remove this command from recipes to be able to delete it!");
        }

        command.setDeactivated(true);
        return commandRepository.save(command);
    }

    @Override
    public Command updateCommand(String commandId, Command updateCommand) {
        Command command = getCommandById(commandId);

        if (updateCommand == null) {
            throw new InvalidEntityException("Command with changes is null!");
        }

        if (updateCommand.getName() != null) {
            command.setName(updateCommand.getName());
        }
        if (updateCommand.getParams() != null) {
            command.setParams(updateCommand.getParams());
        }
        if (updateCommand.getDeviceType() != null && updateCommand.getDeviceType() != command.getDeviceType()) {
            if (checkIfCommandUsedAsSubCommand(command)) {
                throw new InvalidOperationException("Remove this command from recipes to be able to change its type!");
            }
            command.setDeviceType(updateCommand.getDeviceType());
        }

        if (updateCommand.getSubCommands() != null) {
            command.setSubCommands(new ArrayList<>());
            command = addAllSubCommandsToCommand(command, updateCommand.getSubCommands());
        }

        return commandRepository.save(command);
    }

    private Command addAllSubCommandsToCommand(Command comand, List<Command> subCommands) {
        for (Command subCommand : subCommands) {
            comand = addSubCommandToCommand(comand, subCommand);
        }
        return comand;
    }

    @Override
    public Command addSubCommandToCommand(Command command, Command subCommand) {
        if (Objects.equals(command.getId(), subCommand.getId())) {
            throw new InvalidOperationException("Recipe can't be added as its own sub-recipe to prevent infinite loop!");
        }

        if (command.getDeviceType() != subCommand.getDeviceType()) {
            throw new InvalidEntityException("Device types of the recipes do not match!"
            + " Recipe's device type: " + command.getDeviceType()
            + ", Sub-recipe's device type: " + subCommand.getDeviceType());
        }

        if (subCommand.getSubCommands() != null) {
            List<String> subCommandIds = subCommand.getSubCommands().stream().map(Command::getId).toList();
            if (subCommandIds.contains(command.getId())) {
                throw new InvalidOperationException("The list of sub-recipes of the given sub-recipe contains the recipe," +
                        " therefore it can't be used as sub-recipe of the recipe to prevent infinite loop.");
            }
        }

        if (command.getSubCommands() == null) {
            command.setSubCommands(new ArrayList<>());
        }

        command.getSubCommands().add(subCommand);
        return commandRepository.save(command);
    }


    private boolean checkIfCommandUsedAsSubCommand(Command command) {
        List<Command> commandsContainingSubCommand = getCommandsContainingSubCommand(command);

        return !commandsContainingSubCommand.isEmpty();
    }

    private List<Command> getCommandsContainingSubCommand(Command subCommand) {
        return commandRepository.getCommandsBySubCommandsContainingAndDeactivated(subCommand, false);
    }
}
