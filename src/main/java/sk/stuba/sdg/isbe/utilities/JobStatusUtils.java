package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

public final class JobStatusUtils {

    public static JobStatusEnum getJobStatusEnum(String jobStatus) {
        try {
            return Enum.valueOf(JobStatusEnum.class, jobStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Job status type: '" + jobStatus + "' does not exist!");
        }
    }
}
