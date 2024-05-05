package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("!deployment")
public class JobServiceTests {

    @Autowired
    private JobService jobService;

    @Autowired
    protected JobRepository jobRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Autowired
    private JobStatusService jobStatusService;

    private static final String NONE = "NONE";

    // @Test
    // void testRunJobFromRecipe() {
    //     Recipe recipe = new Recipe("Recipe" + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, true);
    //     recipeService.createRecipe(recipe);

    //     Device device = new Device("device1" + Instant.now().toEpochMilli(), "ABCD", DeviceTypeEnum.ESP32);
    //     deviceService.createDevice(device, null);

    //     recipe.setSubRecipe(false);
    //     recipeService.updateRecipe(recipe.getId(), recipe);
    //     Exception exception = assertThrows(InvalidEntityException.class, () -> jobService.runJobFromRecipe(recipe.getId(), device.getUid(), 1, null, null, null));
    //     assertEquals("The recipe and its sub-recipes do not contain any commands!", exception.getMessage());

    //     Command command = new Command("Command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32);
    //     commandService.createCommand(command);

    //     Command command2 = new Command("Command2" + Instant.now().toEpochMilli(), List.of(4.0,5.0,6.0), DeviceTypeEnum.ESP32);
    //     commandService.createCommand(command2);

    //     Command command3 = new Command("Command3" + Instant.now().toEpochMilli(), List.of(7.0,8.0,9.0), DeviceTypeEnum.ESP32);
    //     commandService.createCommand(command3);

    //     recipeService.addCommandToRecipe(recipe.getId(), command.getId());
    //     recipeService.addCommandToRecipe(recipe.getId(), command.getId());
    //     recipe.setCommands(List.of(command));
    //     recipeService.updateRecipe(recipe.getId(), recipe);

    //     exception = assertThrows(InvalidOperationException.class, () -> jobService.runJobFromRecipe(recipe.getId(), device.getUid(), -1, null, null, null));
    //     assertEquals("Repetitions must be equal to or greater than 0!", exception.getMessage());

    //     Recipe subRecipe = new Recipe("SubRecipe" + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, true);
    //     subRecipe.setCommands(List.of(command2));
    //     recipeService.createRecipe(subRecipe);
    //     recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());
    //     recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());

    //     Recipe subSubRecipe = new Recipe("SubSubRecipe" + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
    //     subSubRecipe.setCommands(List.of(command3));
    //     recipeService.createRecipe(subSubRecipe);
    //     recipeService.addSubRecipeToRecipe(subRecipe.getId(), subSubRecipe.getId());

    //     Job job = jobService.runJobFromRecipe(recipe.getId(), device.getUid(), 1, null, null, null);
    //     assertEquals(5, job.getNoOfCmds());

    //     commandRepository.delete(command);
    //     commandRepository.delete(command2);
    //     commandRepository.delete(command3);
    //     recipeRepository.delete(recipe);
    //     recipeRepository.delete(subRecipe);
    //     recipeRepository.delete(subSubRecipe);
    //     jobRepository.delete(job);
    //     jobStatusRepository.delete(job.getStatus());
    //     deviceRepository.delete(device);
    // }

    @Test
    void testResetJob() {
        Device device = new Device("device1" + Instant.now().toEpochMilli(), "ABCD", DeviceTypeEnum.ESP32);
        deviceService.createDevice(device, null);

        Command command = new Command("Command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);
        commandService.createCommand(command);

        Job job = new Job("Job " + Instant.now().toEpochMilli(), List.of(command));

        jobService.runJob(job, device.getUid(), 1, null, null, null);
        job = jobService.resetJob(job.getUid());

        jobStatusRepository.delete(job.getStatus());
        jobRepository.delete(job);
        commandRepository.delete(command);
        deviceRepository.delete(device);
    }

    @Test
    void testSkipCycle() {
        Command command = new Command("Command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);

        Device device = new Device("device1" + Instant.now().toEpochMilli(), "ABCD", DeviceTypeEnum.ESP32);
        deviceService.createDevice(device, null);

        Job job = new Job("Job" + Instant.now().toEpochMilli(), List.of(command));

        jobService.runJob(job, device.getUid(), 0, null, null, null);
        job.getStatus().setCurrentCycle(1);
        jobStatusService.upsertJobStatus(job.getStatus());
        Job jobDb = jobService.skipCycle(job.getUid());
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentCycle());

        jobRepository.delete(job);
        jobStatusRepository.delete(job.getStatus());
        deviceRepository.delete(device);
    }

    @Test
    void testSkipStep() {
        Command command = new Command("Command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);

        Device device = new Device("device1" + Instant.now().toEpochMilli(), "ABCD", DeviceTypeEnum.ESP32);
        deviceService.createDevice(device, null);

        Job job = new Job("Job" + Instant.now().toEpochMilli(), List.of(command));

        jobService.runJob(job, device.getUid(), 0, null, null, null);
        job.getStatus().setTotalSteps(3);
        job.getStatus().setCurrentStep(1);
        jobStatusService.upsertJobStatus(job.getStatus());
        Job jobDb = jobService.skipStep(job.getUid());
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentStep());

        jobRepository.delete(job);
        jobStatusRepository.delete(job.getStatus());
        deviceRepository.delete(device);
    }

    @Test
    void testGetJobsByStatus() {
        Command command = new Command("Command" + Instant.now().toEpochMilli(), List.of(1.0,2.0,3.0), DeviceTypeEnum.ESP32, false);

        Device device = new Device("device1" + Instant.now().toEpochMilli(), "ABCD", DeviceTypeEnum.ESP32);
        deviceService.createDevice(device, null);

        Job job = new Job("Job" + Instant.now().toEpochMilli(), List.of(command));
        Job job2 = new Job("Job" + Instant.now().toEpochMilli(), List.of(command));

        jobService.runJob(job, device.getUid(), 0, null, null, null);
        jobService.runJob(job2, device.getUid(), 0, null, null, null);

        Exception exception = assertThrows(NotFoundCustomException.class, () -> jobService.getAllJobsByStatus(device.getUid(), "WRONG_STATUS", NONE, NONE));
        assertEquals("Job status type: '" + "WRONG_STATUS" + "' does not exist!", exception.getMessage());

        List<Job> pendingJobs = jobService.getAllJobsByStatus(device.getUid(), JobStatusEnum.JOB_PENDING.name(), NONE, NONE);
        assertFalse(pendingJobs.isEmpty());

        jobRepository.delete(job);
        jobRepository.delete(job2);
        jobStatusRepository.delete(job.getStatus());
        jobStatusRepository.delete(job2.getStatus());
        deviceRepository.delete(device);
    }
}
