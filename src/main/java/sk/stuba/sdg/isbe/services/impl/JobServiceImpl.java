package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;
import sk.stuba.sdg.isbe.utilities.JobStatusUtils;
import sk.stuba.sdg.isbe.utilities.SortingUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private JobStatusService jobStatusService;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Override
    public Job runJobFromRecipe(String recipeId, String deviceId, int repetitions, List<Integer> scheduledDays, Integer scheduledHour, Integer scheduledMinute) {
        Command recipe = commandService.getCommandById(recipeId);
        if (recipe.isDeactivated()) {
            throw new InvalidEntityException("Recipe is deactivated, can't create a job from it!");
        }

        Job job = new Job(recipe.getName(), getCommandsFromRecipes(recipe));
        return runJob(job, deviceId, repetitions, scheduledDays, scheduledHour, scheduledMinute);
    }

    @Override
    public Job runJob(Job job, String deviceId, int repetitions, List<Integer> scheduledDays, Integer scheduledHour, Integer scheduledMinute) {
        if (repetitions < 0) {
            throw new InvalidOperationException("Repetitions must be equal to or greater than 0!");
        }

        job.setNoOfReps(repetitions);
        job.setScheduledDays(scheduledDays);
        job.setScheduledHour(scheduledHour);
        job.setScheduledMinute(scheduledMinute);
        validateJob(job);

        Device device = deviceService.getDeviceById(deviceId);

        // try if jobs stack is full
        if (device.getJobs().size() >= 10) {
            throw new InvalidEntityException("Job can't be added! Job stack of device is full.");
        }

        //save job to get an ID for it
        job = jobRepository.save(job);

        job.setStatus(createJobStatusForJob(device, job));
        job.setDeviceId(deviceId);
        job.setCurrentStatus(JobStatusEnum.JOB_PENDING);
        job.setCreatedAt(Instant.now().toEpochMilli());

        // add job as running on device by device uid
        deviceService.addJobToDevice(deviceId, job.getUid());
        return jobRepository.save(job);
    }

    private JobStatus createJobStatusForJob(Device device, Job job) {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        // add dataPoints
        jobStatus.setData(getDataPointsFromDevice(device));
        jobStatus.setJobId(job.getUid());
        jobStatus.setTotalSteps(job.getNoOfCmds());
        return jobStatusService.createJobStatus(jobStatus);
    }

    @Override
    public Job resetJob(String jobId) {
        Job job = getJobById(jobId);
        Device device = deviceService.getDeviceById(job.getDeviceId());

        jobStatusRepository.delete(job.getStatus());

        job.setCurrentStatus(JobStatusEnum.JOB_PENDING);
        job.setStatus(createJobStatusForJob(device, job));

        job.setCreatedAt(Instant.now().toEpochMilli());
        jobRepository.save(job);

        return job;
    }

    private List<DataPoint> getDataPointsFromDevice(Device device) {
        List<DataPoint> dataPoints = new ArrayList<>();

        for (DataPointTag tag : device.getDataPointTags()) {
            DataPoint dataPoint = new DataPoint();
            dataPoint.setTag(tag.getTag());
            dataPoints.add(dataPoint);
        }
        return dataPoints;
    }

    private List<Command> getCommandsFromRecipes(Command recipe) {
        List<Command> commands = new ArrayList<>();
        addCommandsRecursively(commands, recipe);

        if (commands.isEmpty()) {
            throw new InvalidEntityException("The recipe and its sub-recipes do not contain any commands!");
        }
        return commands;
    }

    private void addCommandsRecursively(List<Command> commands, Command recipe) {
        
        //Is actual command with params
        if(!recipe.isRecipe()) {
            commands.add(recipe);
        }

        if (recipe.getSubCommands() == null) {
            return;
        }

        for (Command subRecipe : recipe.getSubCommands()) {
            addCommandsRecursively(commands, subRecipe);
        }
    }

    @Override
    public Job skipCycle(String jobId) {
        Job job = getJobById(jobId);
        Integer currentCycle = job.getStatus().getCurrentCycle();
        Integer maxCycles = job.getNoOfReps();

        if (Objects.equals(currentCycle, maxCycles)) {
            throw new InvalidOperationException("Last cycle is under process, can't skip current one!");
        }
        if(currentCycle == null) {
            currentCycle = 0;
        }
        job.getStatus().setCurrentStep(0);
        job.getStatus().setCurrentCycle(currentCycle + 1);
        jobStatusService.upsertJobStatus(job.getStatus());

        return job;
    }

    @Override
    public Job skipStep(String jobId) {
        Job job = getJobById(jobId);
        Integer currentStep = job.getStatus().getCurrentStep();
        Integer maxSteps = job.getStatus().getTotalSteps();

        if (Objects.equals(currentStep, maxSteps)) {
            throw new InvalidOperationException("Last step is under process, can't skip current one!");
        }
        if(currentStep == null) {
            currentStep = 0;
        }
        job.getStatus().setCurrentStep(currentStep + 1);
        jobStatusService.upsertJobStatus(job.getStatus());

        return job;
    }

    @Override
    public Job cancelJob(String jobId) {
        Job job = getJobById(jobId);
        job.setToCancel(true);
        return jobRepository.save(job);
    }

    @Override
    public Job pauseJob(String jobId) {
        return setJobPaused(jobId, true);
    }

    @Override
    public Job continueJob(String jobId) {
        return setJobPaused(jobId, false);
    }

    private Job setJobPaused(String jobId, boolean paused) {
        Job job = getJobById(jobId);
        job.setPaused(paused);
        return jobRepository.save(job);
    }

    @Override
    public String getJobStatus(String jobId) {
        Job job = getJobById(jobId);
        switch(job.getCurrentStatus()) {
            case JOB_PENDING -> {
                return JobStatusEnum.JOB_PENDING.name() + ": Job '" + job.getName() + "' is pending!";
            }
            case JOB_PROCESSING -> {
                return JobStatusEnum.JOB_PROCESSING.name() + ": Job '" + job.getName() + "' is being processed!";
            }
            case JOB_ERR -> {
                return JobStatusEnum.JOB_ERR.name() + ": Job '" + job.getName() + "' ended with error!";
            }
            case JOB_DONE -> {
                return JobStatusEnum.JOB_DONE.name() + ": Job '" + job.getName() + "' ended successfully!";
            }
            case JOB_IDLE -> {
                return JobStatusEnum.JOB_IDLE.name() + ": Job '" + job.getName() + "' is idle!";
            }
            case JOB_CANCELED -> {
                return JobStatusEnum.JOB_CANCELED.name() + ": Job '" + job.getName() + "' has been canceled by user!";
            }
            case JOB_FREE -> {
                return JobStatusEnum.JOB_FREE.name() + ": Job '" + job.getName() + "' is free!";
            }
            case JOB_PAUSED -> {
                return JobStatusEnum.JOB_PAUSED.name() + ": Job '" + job.getName() + "' has been paused by user!";
            }
        }
        return "Status of job '" + job.getName() + "' is " + job.getCurrentStatus().name();
    }

    @Override
    public List<Job> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any jobs !");
        }
        return jobs;
    }

    @Override
    public List<Job> getAllJobsOnDevice(String deviceId, String sortBy, String sortDirection) {
        Device device = deviceService.getDeviceById(deviceId);
        List<Job> jobs = jobRepository.getJobsByDeviceId(deviceId, SortingUtils.getSort(Job.class, sortBy, sortDirection));
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any jobs on the device with name: '" + device.getName() + "'!");
        }
        return jobs;
    }

    @Override
    public List<Job> getAllJobsOnDevicePageable(String deviceId, int page, int pageSize, String sortBy, String sortDirection) {
        Device device = deviceService.getDeviceById(deviceId);
        Pageable pageable = SortingUtils.getFirstEntry(Job.class);
        List<Job> jobs = jobRepository.getJobsByDeviceId(deviceId, pageable);
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any jobs on the device: " + device.getName() + "!");
        }

        pageable = SortingUtils.getPagination(Job.class, sortBy, sortDirection, page, pageSize);
        jobs = jobRepository.getJobsByDeviceId(deviceId, pageable);
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any jobs on page " + page + " for device with name: '" + device.getName() + "'!");
        }
        return jobs;
    }

    @Override
    public List<Job> getFinishedJobsByStatus(String deviceId, String sortBy, String sortDirection) {
        return getAllJobsByStatus(deviceId, JobStatusEnum.JOB_DONE.name(), sortBy, sortDirection);
    }

    @Override
    public List<Job> getRunningJobsByStatus(String deviceId, String sortBy, String sortDirection) {
        return getAllJobsByStatus(deviceId, JobStatusEnum.JOB_PROCESSING.name(), sortBy, sortDirection);
    }

    @Override
    public Job getJobById(String jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            throw new NotFoundCustomException("Job with ID: '" + jobId + "' was not found!");
        }
        return optionalJob.get();
    }

    @Override
    public List<Job> getJobsByName(String name) {
        List<Job> jobs = jobRepository.getJobsByName(name);
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("Jobs with name: '" + name + "' were not found!");
        }
        return jobs;
    }

    @Override
    public List<Job> getAllJobsByStatus(String deviceId, String statusName, String sortBy, String sortDirection) {
        JobStatusEnum jobStatus = JobStatusUtils.getJobStatusEnum(statusName);
        Device device = deviceService.getDeviceById(deviceId);
        List<Job> jobs = jobRepository.getJobsByDeviceIdAndCurrentStatusIs(deviceId, jobStatus, SortingUtils.getSort(Job.class, sortBy, sortDirection));

        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("No jobs found with status: '" + statusName + "' on device: '" + device.getName() + "'!");
        }
        return jobs;
    }

    @Override
    public List<Job> getAllJobsByStatusPageable(String deviceId, String status, int page, int pageSize, String sortBy, String sortDirection) {
        JobStatusEnum jobStatus = JobStatusUtils.getJobStatusEnum(status);
        Device device = deviceService.getDeviceById(deviceId);
        Pageable pageable = SortingUtils.getFirstEntry(Job.class);
        List<Job> jobs = jobRepository.getJobsByDeviceIdAndCurrentStatusIs(deviceId, jobStatus, pageable);
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any jobs with status '" + status + "' on the device: '" + device.getName() + "'!");
        }

        pageable = SortingUtils.getPagination(Job.class, sortBy, sortDirection, page, pageSize);
        jobs = jobRepository.getJobsByDeviceIdAndCurrentStatusIs(deviceId, jobStatus, pageable);
        if (jobs.isEmpty()) {
            throw new NotFoundCustomException("No jobs found with status: '" + status + "' on device: '" + device.getName() + "' on page " + page + "!");
        }
        return jobs;
    }

    private void validateJob(Job job) {
        if (job.getName() == null || job.getName().isEmpty()) {
            throw new InvalidEntityException("Job's name is not valid!");
        }
        if (job.getCommands() == null || job.getCommands().isEmpty()) {
            throw new InvalidEntityException("Job does not contain any commands!");
        }
        if (job.getNoOfReps() == null || job.getNoOfReps() < 0) {
            throw new InvalidEntityException("Repetitions must be equal to or greater than 0!");
        }

        if (job.getScheduledDays() == null) {
            return;
        }
        job.setScheduledDays(job.getScheduledDays().stream().filter(Objects::nonNull).toList());
        if (job.getScheduledDays().isEmpty()) {
            return;
        }
        if (job.getScheduledHour() != null) {
            if (job.getScheduledHour() < 0 || job.getScheduledHour() > 23) {
                throw new InvalidEntityException("Scheduled hour is invalid: " + job.getScheduledHour() + "!");
            }
        }
        if (job.getScheduledMinute() != null) {
            if (job.getScheduledMinute() < 0 || job.getScheduledMinute() > 59) {
                throw new InvalidEntityException("Scheduled minute is invalid: " + job.getScheduledMinute() + "!");
            }
        }
        for(Integer day : job.getScheduledDays()) {
            if (day < 1 || day > 7) {
                int dayIndex = job.getScheduledDays().indexOf(day);
                throw new InvalidEntityException("Scheduled day on index '" + dayIndex + "' is" +
                        " not valid. Day("+ dayIndex +") = " + day);
            }
        }
    }
}
