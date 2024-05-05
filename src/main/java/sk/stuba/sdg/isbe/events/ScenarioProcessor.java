package sk.stuba.sdg.isbe.events;

import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.LastStoredData;
import sk.stuba.sdg.isbe.domain.model.Scenario;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.repositories.LastStoredDataRepository;
import sk.stuba.sdg.isbe.repositories.ScenarioRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.LastStoredDataService;
import sk.stuba.sdg.isbe.services.ScenarioService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ScenarioProcessor {

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    StoredDataRepository storedDataRepository;

    @Autowired
    LastStoredDataService lastStoredDataService;

    @Autowired
    LastStoredDataRepository lastStoredDataRepository;

    @Autowired
    JobService jobService;

    JsonLogic jsonLogic = new JsonLogic();

    @Async
    @EventListener
    public void handleDataSavedEvent(DataStoredEvent event) throws JsonLogicException {
        Calendar calendar = Calendar.getInstance();
        int actualDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int actualHour = calendar.get(Calendar.HOUR_OF_DAY);

        List<Scenario> scenarios = scenarioRepository.getScenarioByDevicesContainingAndDeactivated(event.getDeviceId(), false);
        if (scenarios != null) {
            for (StoredData storedData : event.getStoredData()) {
                lastStoredDataService.updateLastStoredData(storedData.getTag(), storedData.getValue(), storedData.getDeviceId());
                Map<String, Double> dataForExpression = new HashMap<>();
                for (Scenario scenario : scenarios) {
                    // FILTERING
                    //check, if scenario has all data needed for evaluation
                    AtomicBoolean hasAllDataForEvaluation = new AtomicBoolean(true);
                    // check, if scenario is active only in some days
                    if (scenario.getActiveAtDay().isEmpty() || scenario.getActiveAtDay().contains(actualDayOfWeek)) {
                        // check, if scenario is active only at some hours
                        if (scenario.getActiveAtHour().isEmpty() || scenario.getActiveAtHour().contains(actualHour)) {
                            // check, if scenario associated with deviceId should be evaluating for this storedData
                            if (scenario.getDeviceAndTag().get(event.getDeviceId()).contains(storedData.getTag())) {
                                // check, if scenario is muted
                                if (scenario.getMutedUntil() == null || (Instant.now().toEpochMilli() > scenario.getMutedUntil())) {
                                    Map<String, List<String>> mapDeviceAndTag = scenario.getDeviceAndTag();
                                    dataForExpression.put(storedData.getDeviceId() + storedData.getTag(), storedData.getValue());
                                    mapDeviceAndTag.forEach((k, v) -> {
                                        for (String tag : v) {
                                            if (k.equals(event.getDeviceId()) && tag.equals(storedData.getTag())) {
                                                // we can skip one DB call, because we already put actual storedData to dataForExpression
                                            } else {
                                                LastStoredData lastStoredData = lastStoredDataRepository.findByDeviceIdAndTag(k, tag);
                                                if (lastStoredData != null) {
                                                    dataForExpression.put(lastStoredData.getDeviceId() + lastStoredData.getTag(), lastStoredData.getValue());
                                                } else {
                                                    hasAllDataForEvaluation.set(false);
                                                }
                                            }

                                        }
                                    });

                                    if (hasAllDataForEvaluation.get()) {
                                        // desatinne cisla musia byt pisane s . -> 4.1
                                        String result = (String) jsonLogic.apply(scenario.getRules(), dataForExpression);
                                        if (scenario.getMutedUntil() != null) {
                                            scenario.setMutedUntil(null);
                                        }
                                        if (result.contains(EventConstants.NO_ACTION)) {
                                        } else if (result.contains(EventConstants.FOR_TIME)) {
                                            handleScenarioForTime(scenario, result);
                                        } else if (result.contains(EventConstants.NOTIFICATION_MESSAGE)) {
                                            handleNotificationMessage(scenario, result);
                                        } else if (result.contains(EventConstants.JOB)) {
                                            handleScenarioJob(scenario, result);
                                        } else {
                                            throw new InvalidOperationException("Result: " + result + " not recognized.");
                                        }
                                    }
                                    dataForExpression.clear();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Long calculateUntilTime(Long activatedAt, Long forTime, String timeUnit) {

        long timeToAdd = switch (timeUnit) {
            case EventConstants.SEC -> forTime * 1000;
            case EventConstants.MIN -> forTime * 60 * 1000;
            case EventConstants.HOUR -> forTime * 60 * 60 * 1000;
            default -> throw new InvalidOperationException("Invalid time unit: " + timeUnit);
        };

        return activatedAt + timeToAdd;
    }

    public void handleNotificationMessage(Scenario scenario, String result) {

        String message = result.split(":")[1];

        //trigger time (multiple values) for certain message
        if (scenario.getMessageAndTriggerTime().containsKey(message)) {
            scenario.getMessageAndTriggerTime().get(message).add(Instant.now().toEpochMilli());
        } else {
            List<Long> triggeredAt = new ArrayList<>();
            triggeredAt.add(Instant.now().toEpochMilli());
            scenario.getMessageAndTriggerTime().put(message, triggeredAt);
        }
        //trigger count for certain message
        if (scenario.getMessageMultiplicityCounter().containsKey(message)) {
            scenario.getMessageMultiplicityCounter().put(message, scenario.getMessageMultiplicityCounter().get(message) + 1);
        } else {
            scenario.getMessageMultiplicityCounter().put(message, 1);
        }

        if (!scenario.getAlreadyTriggered()) {
            scenario.setAlreadyTriggered(true);
        }
        scenarioService.editScenario(scenario);
    }

    public void handleScenarioJob(Scenario scenario, String result) {
        String jobId = result.split(":")[1];

        Job job = jobService.getJobById(jobId);
        if (job != null) {
            if (job.getCurrentStatus().equals(JobStatusEnum.JOB_DONE) || job.getCurrentStatus().equals(JobStatusEnum.JOB_ERR)) {
                jobService.resetJob(jobId);
                //trigger time (multiple values) for certain job
                if (scenario.getJobAndTriggerTime().containsKey(jobId)) {
                    scenario.getJobAndTriggerTime().get(jobId).add(Instant.now().toEpochMilli());
                } else {
                    List<Long> triggeredAt = new ArrayList<>();
                    triggeredAt.add(Instant.now().toEpochMilli());
                    scenario.getJobAndTriggerTime().put(jobId, triggeredAt);
                }
                if (!scenario.getAlreadyTriggered()) {
                    scenario.setAlreadyTriggered(true);
                }
                scenarioService.editScenario(scenario);
            }
        }
    }

    public void handleScenarioForTime(Scenario scenario, String result) {

        String forTimeSubType = result.split(":")[1];

        if (forTimeSubType.equals(EventConstants.FOR_TIME_SET)) {

            String scenarioReturnStatement = result.split(":")[4] + ":" + result.split(":")[5];

            //check if return statement was ever triggered
            if (scenario.getForTimeCountingActivatedAt().containsKey(scenarioReturnStatement)) {
                Long timeUntil = calculateUntilTime(scenario.getForTimeCountingActivatedAt().get(scenarioReturnStatement), Long.valueOf(result.split(":")[2]), result.split(":")[3]);

                if (Instant.now().toEpochMilli() >= timeUntil) {
                    if (result.contains(EventConstants.NOTIFICATION_MESSAGE)) {
                        handleNotificationMessage(scenario, scenarioReturnStatement);
                    } else if (result.contains(EventConstants.JOB)) {
                        handleScenarioJob(scenario, scenarioReturnStatement);
                    }
                }
            } else {
                scenario.getForTimeCountingActivatedAt().put(scenarioReturnStatement, Instant.now().toEpochMilli());
                scenarioService.editScenario(scenario);
            }
        } else if (forTimeSubType.equals(EventConstants.FOR_TIME_RESET)) {
            String scenarioReturnStatement = result.split(":")[2] + ":" + result.split(":")[3];
            if (!scenario.getForTimeCountingActivatedAt().isEmpty()) {
                scenario.getForTimeCountingActivatedAt().remove(scenarioReturnStatement);
            }
            scenarioService.editScenario(scenario);
        }
    }
}