package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import com.google.protobuf.Timestamp;
import sk.stuba.sdg.isbe.domain.model.protofiles.DeviceBuff;
import sk.stuba.sdg.isbe.domain.model.protofiles.JobStatusBuff;

import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;


public class DevicebuffConverters {

    public static DeviceBuff.Device toProtobufDevice(sk.stuba.sdg.isbe.domain.model.Device device) {
        DeviceBuff.Device.Builder deviceBuilder = DeviceBuff.Device.newBuilder()
                .setUid(device.getUid())
                .setName(device.getName())
                .setMac(device.getMac())
                .setResponseTime(device.getResponseTime())
                .setInitApiKey(device.getInitApiKey())
                .setDeactivated(device.isDeactivated());

        // Convert jobs
        List<DeviceBuff.Job> protobufJobs = device.getJobs().stream()
                .map(DevicebuffConverters::toProtobufJob)
                .collect(Collectors.toList());
        deviceBuilder.addAllJobs(protobufJobs);

        return deviceBuilder.build();
    }

    private static DeviceBuff.Job toProtobufJob(sk.stuba.sdg.isbe.domain.model.Job job) {
        DeviceBuff.Job.Builder jobBuilder = DeviceBuff.Job.newBuilder()
                .setUid(job.getUid())
                .setDeviceId(job.getDeviceId())
                .setName(job.getName())
                .setNoOfCmds(job.getNoOfCmds())
                .setNoOfReps(job.getNoOfReps())
                .setCurrentStatus(toProtobufJobStatusEnum(job.getCurrentStatus()))
                .addAllCommands(job.getCommands().stream().map(DevicebuffConverters::toProtobufCommand).collect(Collectors.toList()))
                .setToCancel(job.isToCancel())
                .setPaused(job.isPaused())
                .addAllScheduledDays(job.getScheduledDays())
                .setScheduledHour(job.getScheduledHour())
                .setScheduledMinute(job.getScheduledMinute());

        // Convert JobStatus if present
        if (job.getStatus() != null) {
            jobBuilder.setStatus(toProtobufJobStatus(job.getStatus()));
        }

        // Timestamp conversion as before
        if (job.getCreatedAt() != null) {
            jobBuilder.setCreatedAt(toProtobufTimestamp(new Date(job.getCreatedAt())));
        }
        if (job.getStartedAt() != null) {
            jobBuilder.setStartedAt(toProtobufTimestamp(new Date(job.getStartedAt())));
        }
        if (job.getFinishedAt() != null) {
            jobBuilder.setFinishedAt(toProtobufTimestamp(new Date(job.getFinishedAt())));
        }

        return jobBuilder.build();
    }

    private static Timestamp toProtobufTimestamp(LocalDateTime localDateTime) {
        // Convert LocalDateTime to Instant with UTC zone and then to Timestamp
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    // Assuming you have a JobStatus class in your Java model similar to your Protobuf JobStatus
    private static JobStatusBuff.JobStatus toProtobufJobStatus(sk.stuba.sdg.isbe.domain.model.JobStatus jobStatus) {
        JobStatusBuff.JobStatus.Builder statusBuilder = JobStatusBuff.JobStatus.newBuilder()
                .setUid(jobStatus.getUid())
                .setJobId(jobStatus.getJobId())
                .setRetCode(toProtobufJobStatusEnum(jobStatus.getRetCode()))
                .setCode(toProtobufJobStatusEnum(jobStatus.getCode()))
                .setCurrentStep(jobStatus.getCurrentStep())
                .setTotalSteps(jobStatus.getTotalSteps())
                .setCurrentCycle(jobStatus.getCurrentCycle())
                .addAllData(jobStatus.getData().stream().map(DevicebuffConverters::toProtobufDataPoint).collect(Collectors.toList()));

        if (jobStatus.getLastUpdated() != null) {
            statusBuilder.setLastUpdated(toProtobufTimestamp(jobStatus.getLastUpdated()));
        }

        return statusBuilder.build();
    }

    private static JobStatusBuff.DataPoint toProtobufDataPoint(sk.stuba.sdg.isbe.domain.model.DataPoint dataPoint) {
        return JobStatusBuff.DataPoint.newBuilder()
                .setTag(dataPoint.getTag())
                .setValue(dataPoint.getValue())
                .build();
    }

    private static JobStatusBuff.JobStatusEnum toProtobufJobStatusEnum(JobStatusEnum status) {
        // Assuming JobStatusEnum in Java matches the enum in Protobuf
        if (status == null) {
            return JobStatusBuff.JobStatusEnum.forNumber(0);
        }
        return JobStatusBuff.JobStatusEnum.valueOf(status.name());
    }

    private static DeviceBuff.Command toProtobufCommand(sk.stuba.sdg.isbe.domain.model.Command command) {
        DeviceBuff.Command.Builder commandBuilder = DeviceBuff.Command.newBuilder()
                .setId(command.getId())
                .setName(command.getName())
                .addAllParams(command.getParams())
                .setCreatedAt(command.getCreatedAt())
                .setDeactivated(command.isDeactivated());

        return commandBuilder.build();
    }

    private static Timestamp toProtobufTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        long millis = date.getTime();
        return Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
    }
}
