package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.LastStoredData;

public interface LastStoredDataService {

    void updateLastStoredData(String dataPointTagId, Double value, String deviceId);
}
