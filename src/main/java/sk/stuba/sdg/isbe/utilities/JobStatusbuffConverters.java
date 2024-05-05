package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.model.protofiles.JobStatusBuff;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.DataPoint;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

public class JobStatusbuffConverters {
    public static LocalDateTime toLocalDateTime(com.google.protobuf.Timestamp timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneOffset.UTC);
    }

    public static JobStatus convertToDomainJobStatus(JobStatusBuff.JobStatus protoJobStatus) {
        JobStatus domainJobStatus = new JobStatus();

        // Set UID
        if (!protoJobStatus.getUid().isEmpty()) {
            domainJobStatus.setUid(protoJobStatus.getUid());
        }

        // Set JobId
        if (!protoJobStatus.getJobId().isEmpty()) {
            domainJobStatus.setJobId(protoJobStatus.getJobId());
        }

        if(!protoJobStatus.getDeviceKey().isEmpty()) {
            domainJobStatus.setDeviceKey(protoJobStatus.getDeviceKey());
        }

        // Set RetCode and Code using enum mapping
        domainJobStatus.setRetCode(mapProtoEnumToDomain(protoJobStatus.getRetCode()));
        domainJobStatus.setCode(mapProtoEnumToDomain(protoJobStatus.getCode()));

        // Set currentStep, totalSteps, and currentCycle
        domainJobStatus.setCurrentStep(protoJobStatus.getCurrentStep());

        if (protoJobStatus.getTotalSteps() > 0) {
            domainJobStatus.setTotalSteps(protoJobStatus.getTotalSteps());
        }

        domainJobStatus.setCurrentCycle(protoJobStatus.getCurrentCycle());

        // Convert and set DataPoints
        if (protoJobStatus.getDataCount() > 0) {
            domainJobStatus.setData(protoJobStatus.getDataList().stream()
                    .map(dataPoint -> new DataPoint(dataPoint.getTag(), dataPoint.getValue(),dataPoint.getMeasureAt()))
                    .collect(Collectors.toList()));
        }

        // Convert and set lastUpdated using Timestamp
        if (protoJobStatus.hasLastUpdated()) {
            domainJobStatus.setLastUpdated(toLocalDateTime(protoJobStatus.getLastUpdated()));
        }

        return domainJobStatus;
    }

    private static JobStatusEnum mapProtoEnumToDomain(JobStatusBuff.JobStatusEnum protoEnum) {
        // Assuming you have a mapping method or logic to convert from Protobuf enum to your domain enum
        // This is just a placeholder and needs to be implemented based on your enum mapping
            if (protoEnum == JobStatusBuff.JobStatusEnum.forNumber(0)) {
                return null;
            }
        return JobStatusEnum.valueOf(protoEnum.name());
    }
}
