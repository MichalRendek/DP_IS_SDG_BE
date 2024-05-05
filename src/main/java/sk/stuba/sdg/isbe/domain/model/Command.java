package sk.stuba.sdg.isbe.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

import java.util.List;

@Document
public class Command {

    @Id
    private String id;

    /**
     * Command's name.
     */
    private String name;

    /**
     * Parameters which are executed by the device. These are given by the user when creating a command.
     */
    private List<Double> params;

    /**
     * Device type on which the commands can be executed. Also helps when adding command to a recipe to prevent user from adding a non-supported command.
     */
    private DeviceTypeEnum deviceType;

    /**
     * Command's creation date.
     */
    private long createdAt;

    /**
     * Indicates if the command is a recipe
    */
    private Boolean recipe;

    /**
     * Subcommands can now be part of a single command (acting like sub-recipes).
    */
    @DBRef
    private List<Command> subCommands;

    /**
     * Flag that is set when the user deletes a command. It provides the option to retrieve deleted commands from the database.
     */
    @JsonIgnore
    private boolean deactivated;


    public Command() {}

    public Command(String name, List<Double> params, DeviceTypeEnum deviceType, Boolean recipe) {
        this.name = name;
        this.params = recipe ? null : params;
        this.deviceType = deviceType;
        this.recipe = recipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getParams() {
        return params;
    }

    public void setParams(List<Double> params) {
        this.params = params;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRecipe()
    {
        return recipe;
    }

    public void setRecipe(Boolean recipe)
    {
        this.recipe = recipe;
    }

    public List<Command> getSubCommands() {
        return subCommands;
    }

    public void setSubCommands(List<Command> subCommands) {
        this.subCommands = subCommands;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
