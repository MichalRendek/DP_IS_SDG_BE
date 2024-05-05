package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Command;

import java.util.List;

public interface CommandService {

    Command createCommand(Command command);

    List<Command> getAllCommands(String sortBy, String sortDirection);

    List<Command> getAllCommandsPageable(int page, int pageSize, String sortBy, String sortDirection);

    Command getCommandById(String commandId);

    Command deleteCommand(String commandId);

    Command updateCommand(String commandId, Command updateCommand);

    Command addSubCommandToCommand(Command command, Command subCommand);

    List<Command> getCommandsByDeviceType(String deviceType, String sortBy, String sortDirection);

    List<Command> getCommandsByDeviceTypePageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection);
}
