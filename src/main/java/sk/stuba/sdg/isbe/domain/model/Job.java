package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;

import java.util.List;

@Document
public class Job {

    @Id
    private String uid;

    /**
     * Device's id on which the job is running.
     */
    private String deviceId;

    /**
     * Job's name - set automatically to recipe name if run from recipe.
     */
    private String name;

    /**
     * Number of commands in the job.
     */
    private Integer noOfCmds;

    /**
     * Number of repetitions, how many times should the job repeat itself.
     */
    private Integer noOfReps;

    /**
     * All information regarding the job's status.
     */
    @DBRef
    private JobStatus status;

    /**
     * Current status of job - added for option to be able to query based on this attribute.
     */
    private JobStatusEnum currentStatus;

    /**
     * Commands which are executed on the device when the job is running.
     */
    private List<Command> commands;

    /**
     * Flag to indicate for the device that the job has to be cancelled. Can be set via end-point by user.
     */
    private boolean toCancel;

    /**
     * Flag to indicate for the device that the job has to be paused. Can be set via end-point by user.
     */
    private boolean paused;

    /**
     * Job's creation date - set when job is created by the user.
     */
    private Long createdAt;

    /**
     * Job's start date - set by the device, when the job is run.
     */
    private Long startedAt;

    /**
     * Job's finish date - set by the device, when the job has finished.
     */
    private Long finishedAt;

    /**
     * Days on which the job should be run.
     */
    private List<Integer> scheduledDays;

    /**
     * Hour in which the job should be run.
     */
    private Integer scheduledHour;

    /**
     * Minute in which the job should be run.
     */
    private Integer scheduledMinute;

    public Job() {}

    public Job(String name, List<Command> commands) {
        this.name = name;
        this.commands = commands;
        this.noOfCmds = commands.size();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNoOfCmds() {
        return noOfCmds;
    }

    public void setNoOfCmds(Integer noOfCmds) {
        this.noOfCmds = noOfCmds;
    }

    public Integer getNoOfReps() {
        return noOfReps;
    }

    public void setNoOfReps(Integer noOfReps) {
        this.noOfReps = noOfReps;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(JobStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public boolean isToCancel() {
        return toCancel;
    }

    public void setToCancel(boolean toCancel) {
        this.toCancel = toCancel;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<Integer> getScheduledDays() {
        return scheduledDays;
    }

    public void setScheduledDays(List<Integer> scheduledDays) {
        this.scheduledDays = scheduledDays;
    }

    public Integer getScheduledHour() {
        return scheduledHour;
    }

    public void setScheduledHour(Integer scheduledHour) {
        this.scheduledHour = scheduledHour;
    }

    public Integer getScheduledMinute() {
        return scheduledMinute;
    }

    public void setScheduledMinute(Integer scheduledMinute) {
        this.scheduledMinute = scheduledMinute;
    }
}
