package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.ScenarioLevelEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

public final class ScenarioLevelUtils {

    public static ScenarioLevelEnum getScenarioLevelEnum(String scenarioLevel) {
        try {
            return Enum.valueOf(ScenarioLevelEnum.class, scenarioLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Scenario resolution level: '" + scenarioLevel + "' does not exist!");
        }
    }
}
