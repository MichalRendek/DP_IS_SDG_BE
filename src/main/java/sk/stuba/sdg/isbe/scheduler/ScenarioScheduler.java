package sk.stuba.sdg.isbe.scheduler;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.services.ScenarioService;
import sk.stuba.sdg.isbe.services.StoredResolvedScenarioService;


@Component
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
public class ScenarioScheduler {

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    StoredResolvedScenarioService storedResolvedScenarioService;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbName;

    @Bean
    public LockProvider lockProvider(MongoClient mongo) {
        MongoDatabase database = mongo.getDatabase(mongoDbName);
        return new MongoLockProvider(database);
    }

    // execute every 24 hours
    @Scheduled(cron = "0 0 0 * * ?")
    @SchedulerLock(name = "scheduledTaskStoreScenarioJobTriggers", lockAtMostFor = "20m", lockAtLeastFor = "1m")
    public void storeScenarioJobTriggers() {
        this.scenarioService.storeScenarioJobTriggers();
    }

    // execute once every week - Monday
    @Scheduled(cron = "0 0 0 * * 1")
    @SchedulerLock(name = "scheduledTaskRemoveOldStoredScenarioData", lockAtMostFor = "20m", lockAtLeastFor = "1m")
    public void removeOldStoredScenarioData() {
        this.storedResolvedScenarioService.removeOldStoredScenarioData();
    }

}
