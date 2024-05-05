package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.util.List;

public interface StoredDataService {
    StoredData createStoredData(StoredData dataPointSave);

    List<StoredData> getStoredDatas();

    StoredData getStoredDataById(String dataPointSaveId);

    StoredData updateStoredData(String dataPointSaveId, StoredData changeDataSaveTag);

    StoredData deleteStoredData(String dataPointSaveId);

    StoredData upsertStoredData(StoredData storedData);
}
