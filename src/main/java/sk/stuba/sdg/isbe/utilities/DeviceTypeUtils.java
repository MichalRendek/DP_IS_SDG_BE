package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

public final class DeviceTypeUtils {

    public static DeviceTypeEnum getDeviceTypeEnum(String deviceType) {
        try {
            return Enum.valueOf(DeviceTypeEnum.class, deviceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Type of device: '" + deviceType + "' does not exist!");
        }
    }
}
