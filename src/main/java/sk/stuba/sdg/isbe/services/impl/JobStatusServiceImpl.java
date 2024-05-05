package sk.stuba.sdg.isbe.services.impl;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.events.DataStoredEvent;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DataPointTagRepository;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;
import sk.stuba.sdg.isbe.services.StoredDataService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    StoredDataService storedDataService;

    @Autowired
    private DataPointTagRepository dataPointTagRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public JobStatus createJobStatus(JobStatus jobStatus) {
        jobStatus.setCreatedAt(Instant.now().toEpochMilli());
        jobStatus.setLastUpdated(LocalDateTime.now());
        return upsertJobStatus(jobStatus);
    }

    @Override
    public JobStatus getJobStatus(String jobStatusId) {
        Optional<JobStatus> optionalStatus = jobStatusRepository.findById(jobStatusId);
        if (optionalStatus.isEmpty()) {
            throw new NotFoundCustomException("Job-status with ID: '" + jobStatusId + "' was not found!");
        }
        return optionalStatus.get();
    }

    @Override
    public JobStatus updateJobStatus(String jobStatusId, JobStatus changeJobStatus, String deviceId, String deviceKey) {
        JobStatus jobStatus = getJobStatus(jobStatusId);

        if (changeJobStatus == null) {
            throw new InvalidEntityException("JobStatus with changes is null!");
        }

        if (changeJobStatus.getRetCode() != null) {
            jobStatus.setRetCode(changeJobStatus.getRetCode());
            changeJobsCurrentStatus(jobStatus.getJobId(), changeJobStatus.getRetCode());
        }
        if (changeJobStatus.getCode() != null) {
            jobStatus.setCode(changeJobStatus.getCode());
            changeJobsCurrentStatus(jobStatus.getJobId(), changeJobStatus.getCode());
        }
        if (changeJobStatus.getCurrentStep() != null) {
            if (changeJobStatus.getCurrentStep() > jobStatus.getTotalSteps()) {
                throw new InvalidEntityException("JobStatus current step overflow!");
            }
            jobStatus.setCurrentStep(changeJobStatus.getCurrentStep());
        }
        if (changeJobStatus.getTotalSteps() != null) {
            jobStatus.setTotalSteps(changeJobStatus.getTotalSteps());
        }
        if (changeJobStatus.getCurrentCycle() != null) {
            if (changeJobStatus.getCurrentCycle() > jobService.getJobById(jobStatus.getJobId()).getNoOfReps()) {
                throw new InvalidEntityException("JobStatus current cycle overflow!");
            }
            jobStatus.setCurrentCycle(changeJobStatus.getCurrentCycle());
        }
        if (changeJobStatus.getData() != null) {
            jobStatus.setData(changeJobStatus.getData());

            long measureAtTime = Instant.now().toEpochMilli();

            if (deviceId != null && deviceService.getDeviceByIdAndKey(deviceId, deviceKey) != null) {
                List<DataPointTag> dataPointTags = deviceService.getDeviceById(deviceId).getDataPointTags();
                List<StoredData> listStoredData = new ArrayList<>();
                for (DataPoint dataPoint : jobStatus.getData()) {
                    DataPointTag dataPointTag = dataPointTags.stream()
                            .filter(data -> Objects.equals(data.getTag(), dataPoint.getTag())).findAny()
                            .orElse(null);
                    StoredData storedData = new StoredData();
                    storedData.setDataPointTagId(dataPointTag.getUid());
                    storedData.setTag(dataPointTag.getTag());
                    storedData.setValue(dataPoint.getValue());
                    storedData.setMeasureAt(measureAtTime);
                    storedData.setMeasureAtDevice(dataPoint.getMeasureAt());
                    storedData.setDeviceId(deviceId);
                    storedDataService.upsertStoredData(storedData);
                    dataPointTag.addStoredData(storedData);
                    listStoredData.add(storedData);
                }
                dataPointTagRepository.saveAll(dataPointTags);
                DataStoredEvent dataStoredEvent = new DataStoredEvent(this, listStoredData, deviceId);
                eventPublisher.publishEvent(dataStoredEvent);
            }
        }

        jobStatus.setLastUpdated(LocalDateTime.now());

        Device device = deviceService.getDeviceById(deviceId);
        if (device != null) {
            device.setLastResponse(Instant.now().toEpochMilli());
            deviceRepository.save(device);
        }

        return upsertJobStatus(jobStatus);
    }

    private void changeJobsCurrentStatus(String jobId, JobStatusEnum jobStatus) {
        Job job = jobService.getJobById(jobId);
        job.setCurrentStatus(jobStatus);
        jobRepository.save(job);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }

    @Override
    public JobStatus upsertJobStatus(JobStatus jobStatus) {
        Query query = new Query(Criteria.where("uid").is(jobStatus.getUid()));
        Update update = new Update()
                .set("jobId", jobStatus.getJobId())
                .set("retCode", jobStatus.getRetCode())
                .set("code", jobStatus.getCode())
                .set("currentStep", jobStatus.getCurrentStep())
                .set("totalSteps", jobStatus.getTotalSteps())
                .set("currentCycle", jobStatus.getCurrentCycle())
                .set("data", jobStatus.getData())
                .set("createdAt", jobStatus.getCreatedAt())
                .set("lastUpdated", jobStatus.getLastUpdated());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, JobStatus.class);

        if (updateResult.getMatchedCount() == 0) {
            // if no matching document found, insert a new document
            mongoTemplate.insert(jobStatus);
        } else {
            // if a matching document is found, update the scenario object with the latest
            // data
            jobStatus = mongoTemplate.findOne(query, JobStatus.class);
        }

        return jobStatus;
    }
}
