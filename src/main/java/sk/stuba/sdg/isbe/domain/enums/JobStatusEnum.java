package sk.stuba.sdg.isbe.domain.enums;

public enum JobStatusEnum {
    JOB_FREE, // no in retCode
    JOB_IDLE, // no in retCode
    JOB_PENDING, // no in retCode, if pending send to device by prop only pending jobs
    JOB_PROCESSING, // no in retCode
    JOB_DONE,
    JOB_ERR,
    JOB_PAUSED, // no in retCode
    JOB_CANCELED,
    JOB_STATUS_MAX
}
