package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.ScenarioLevelEnum;
import sk.stuba.sdg.isbe.domain.enums.ScenarioActionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class StoredResolvedScenario {
    @Id
    private String id;

    private String scenarioId;

    /**
     * Define type of the action that was executed after Scenario trigger.
     */
    private ScenarioActionType scenarioActionType;

    /**
     * Time of creation.
     */
    private Long createdAt;

    /**
     * Define Scenario level - response from user to Scenario.
     */
    private ScenarioLevelEnum level;

    /**
     * This map holds all messages that Scenario returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> messageAndTriggerTime = new HashMap<>();

    /**
     * This map holds all messages that Scenario returned (triggered) and counter - how many times was message sent (triggered).
     */
    private Map<String, Integer> messageMultiplicityCounter = new HashMap<>();

    /**
     * This map holds all jobs that Scenario returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> jobAndTriggerTime = new HashMap<>();
    public String getId() {
        return id;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public ScenarioLevelEnum getLevel() {
        return level;
    }

    public void setLevel(ScenarioLevelEnum level) {
        this.level = level;
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

    public Map<String, List<Long>> getJobAndTriggerTime() {
        return jobAndTriggerTime;
    }

    public void setJobAndTriggerTime(Map<String, List<Long>> jobAndTriggerTime) {
        this.jobAndTriggerTime = jobAndTriggerTime;
    }

    public void setMessageMultiplicityCounter(Map<String, Integer> messageMultiplicityCounter) {
        this.messageMultiplicityCounter = messageMultiplicityCounter;
    }

    public ScenarioActionType getScenarioActionType() {
        return scenarioActionType;
    }

    public void setScenarioActionType(ScenarioActionType scenarioActionType) {
        this.scenarioActionType = scenarioActionType;
    }
}
