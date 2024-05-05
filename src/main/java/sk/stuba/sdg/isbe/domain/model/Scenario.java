package sk.stuba.sdg.isbe.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class Scenario {

    /**
     *  WARNING: when changing field names, do NOT forget to change field names also in method upsertScenario()
     */

    public Scenario() {
    }

    public Scenario(String name, List<String> devices, Boolean deactivated, String rules, Boolean isAlreadyTriggered) {
        this.name = name;
        this.devices = devices;
        this.deactivated = deactivated;
        this.rules = rules;
        this.isAlreadyTriggered = isAlreadyTriggered;
    }

    @Id
    private String id;

    /**
     * Rules (Expressions) defined in Scenario.
     */
    private String rules;

    /**
     * Scenario name.
     */
    private String name;

    /**
     * Devices associated with Scenario.
     */
    private List<String> devices;

    /**
     * Define if Scenario is active.
     */
    private Boolean deactivated;

    /**
     * Define if the Scenario was already triggered - sent to user. This reset if users interact with Scenario.
     */
    @JsonProperty("isAlreadyTriggered")
    private Boolean isAlreadyTriggered;

    /**
     * Define time until the Scenario is muted.
     */
    private Long mutedUntil;

    /**
     * Time of creation.
     */
    private Long createdAt;

    /**
     * Define if the Scenario (which contains forTime) has been triggered. Especially we need provide, which forTime return
     * statement was triggered (scenario can have multiple forTime and each one of them needs own counter).
     * This map also define time when was scenario first time triggered.
     * Final state (message for user/run job) of scenario will trigger after
     * forTimeCounterActivatedAt+forTime(defined by user) has passed and all data sent to scenario meet the
     * requirements to run the counting (requirements are NOT met, when data triggers forTimeReset - then
     * forTimeCountingActivatedAt resets).
     * This resets if scenario is validating data, and these data does not meet the requirements of scenario - then
     * scenario's else statement of IF-check is executed and forTime is reset.
     * ForTime means: data validation in time - multiple data sent in certain time interval to trigger final state of scenario.
     */
    private Map<String, Long> forTimeCountingActivatedAt = new HashMap<>();

    /**
     * This map holds all messages that Scenario returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> messageAndTriggerTime = new HashMap<>();

    /**
     * This map holds all messages that Scenario returned (triggered) and counter - how many times was message sent (triggered).
     */
    private Map<String, Integer> messageMultiplicityCounter = new HashMap<>();

    /**
     * This map holds all tags (DataPointTag) for every device used in certain Scenario.
     */
    private Map<String, List<String>> deviceAndTag = new HashMap<>();

    /**
     * This map holds all jobs that Scenario returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> jobAndTriggerTime = new HashMap<>();

    /**
     * This list define which days is Scenario active - able to evaluate.
     * Range is 1-7.
     * SUNDAY: This value is equal to 1.
     * MONDAY: This value is equal to 2.
     * TUESDAY: This value is equal to 3.
     * WEDNESDAY: This value is equal to 4.
     * THURSDAY: This value is equal to 5.
     * FRIDAY: This value is equal to 6.
     * SATURDAY: This value is equal to 7.
     */
    private List<Integer> activeAtDay = new ArrayList<>();

    /**
     * This list define which hour of the day is Scenario active - able to evaluate.
     * Range is 0-23.
     */
    private List<Integer> activeAtHour = new ArrayList<>();

    public Map<String, List<String>> getDeviceAndTag() {
        return deviceAndTag;
    }

    public void setDeviceAndTag(Map<String, List<String>> deviceAndTag) {
        this.deviceAndTag = deviceAndTag;
    }

    public boolean hasNonEmptyName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasNonEmptyDevices() {
        return devices != null && devices.stream().allMatch(device -> !device.isEmpty());
    }

    public boolean hasNonEmptyRules() {
        return rules != null && !rules.isEmpty();
    }

    public String getId() {
        return id;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public Boolean getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getAlreadyTriggered() {
        return isAlreadyTriggered;
    }

    public void setAlreadyTriggered(Boolean alreadyTriggered) {
        isAlreadyTriggered = alreadyTriggered;
    }

    public Long getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(Long mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    public Map<String, Long> getForTimeCountingActivatedAt() {
        return forTimeCountingActivatedAt;
    }

    public void setForTimeCountingActivatedAt(Map<String, Long> forTimeCountingActivatedAt) {
        this.forTimeCountingActivatedAt = forTimeCountingActivatedAt;
    }

    public Map<String, List<Long>> getMessageAndTriggerTime() {
        return messageAndTriggerTime;
    }

    public void setMessageAndTriggerTime(Map<String, List<Long>> messageAndTriggerTime) {
        this.messageAndTriggerTime = messageAndTriggerTime;
    }

    public Map<String, Integer> getMessageMultiplicityCounter() {
        return messageMultiplicityCounter;
    }

    public void setMessageMultiplicityCounter(Map<String, Integer> messageMultiplicityCounter) {
        this.messageMultiplicityCounter = messageMultiplicityCounter;
    }

    public Map<String, List<Long>> getJobAndTriggerTime() {
        return jobAndTriggerTime;
    }

    public void setJobAndTriggerTime(Map<String, List<Long>> jobAndTriggerTime) {
        this.jobAndTriggerTime = jobAndTriggerTime;
    }

    public List<Integer> getActiveAtDay() {
        return activeAtDay;
    }

    public void setActiveAtDay(List<Integer> activeAtDay) {
        this.activeAtDay = activeAtDay;
    }

    public List<Integer> getActiveAtHour() {
        return activeAtHour;
    }

    public void setActiveAtHour(List<Integer> activeAtHour) {
        this.activeAtHour = activeAtHour;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id='" + id + '\'' +
                ", rules='" + rules + '\'' +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                ", deactivated=" + deactivated +
                ", isAlreadyTriggered=" + isAlreadyTriggered +
                ", mutedUntil=" + mutedUntil +
                ", createdAt=" + createdAt +
                ", forTimeCountingActivatedAt=" + forTimeCountingActivatedAt +
                ", messageAndTriggerTime=" + messageAndTriggerTime +
                ", messageMultiplicityCounter=" + messageMultiplicityCounter +
                ", deviceAndTag=" + deviceAndTag +
                '}';
    }
}
