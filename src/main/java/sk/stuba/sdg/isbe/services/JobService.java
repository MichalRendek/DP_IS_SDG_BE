package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

public interface JobService {

    Job runJobFromRecipe(String recipeId, String deviceId, int repetitions, List<Integer> scheduledDays, Integer scheduledHour, Integer scheduledMinute);

    Job runJob(Job job, String deviceId, int repetitions, List<Integer> scheduledDays, Integer scheduledHour, Integer scheduledMinute);

    Job resetJob(String jobId);

    Job getJobById(String jobId);

    List<Job> getJobsByName(String name);

    Job skipCycle(String jobId);

    Job skipStep(String jobId);

    Job cancelJob(String jobId);

    Job pauseJob(String jobId);

    Job continueJob(String jobId);

    String getJobStatus(String jobId);

    List<Job> getAllJobs();

    List<Job> getAllJobsOnDevice(String deviceId, String sortBy, String sortDirection);

    List<Job> getAllJobsOnDevicePageable(String deviceId, int page, int pageSize, String sortBy, String sortDirection);

    List<Job> getFinishedJobsByStatus(String deviceId, String sortBy, String sortDirection);

    List<Job> getRunningJobsByStatus(String deviceId, String sortBy, String sortDirection);

    List<Job> getAllJobsByStatus(String deviceId, String status, String sortBy, String sortDirection);

    List<Job> getAllJobsByStatusPageable(String deviceId, String status, int page, int pageSize, String sortBy, String sortDirection);

}
