package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.services.CommandService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/command")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @Operation(summary = "Create a command in database using object")
    @PostMapping("createCommand")
    public Command createCommand(@Valid @RequestBody Command command) {
        return commandService.createCommand(command);
    }

    @Operation(summary = "Get command by ID")
    @GetMapping("getCommandById/{commandId}")
    public Command getCommand(@PathVariable String commandId) {
        return commandService.getCommandById(commandId);
    }

    @Operation(summary = "Get all commands")
    @GetMapping("getAllCommands/{sortBy}/{sortDirection}")
    public List<Command> getAllCommands(@PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                        @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return commandService.getAllCommands(sortBy, sortDirection);
    }

    @Operation(summary = "Get all commands with pagination optionally with sorting")
    @GetMapping("getAllCommandsWithPagination/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Command> getAllCommandsPageable(@PathVariable int page, @PathVariable int pageSize,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return commandService.getAllCommandsPageable(page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get command by device-type optionally with sorting")
    @GetMapping("getCommandsByDeviceType/{deviceType}/{sortBy}/{sortDirection}")
    public List<Command> getCommandsByDeviceType(@PathVariable String deviceType,
                                                 @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                 @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return commandService.getCommandsByDeviceType(deviceType, sortBy, sortDirection);
    }

    @Operation(summary = "Get command by device-type and with pagination optionally with sorting")
    @GetMapping("getCommandByDeviceTypeAndPages/{deviceType}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Command> getCommandsByDeviceTypePageable(@PathVariable String deviceType, @PathVariable int page, @PathVariable int pageSize,
                                                         @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                         @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return commandService.getCommandsByDeviceTypePageable(deviceType, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Delete command by ID")
    @DeleteMapping("deleteCommand/{commandId}")
    public Command deleteCommand(@PathVariable String commandId) {
        return commandService.deleteCommand(commandId);
    }

    @Operation(summary = "Update command by ID using command object")
    @PutMapping("updateCommand/{commandId}")
    public Command updateCommand(@PathVariable String commandId, @Valid @RequestBody Command updateCommand) {
        return commandService.updateCommand(commandId, updateCommand);
    }
}
