package sk.stuba.sdg.isbe.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.LastStoredData;
import sk.stuba.sdg.isbe.repositories.LastStoredDataRepository;
import sk.stuba.sdg.isbe.services.LastStoredDataService;

@Service
public class LastStoredDataServiceImpl implements LastStoredDataService {

    @Autowired
    LastStoredDataRepository lastStoredDataRepository;

    @Override
    public void updateLastStoredData(String tag, Double value, String deviceId) {

        LastStoredData data = lastStoredDataRepository.findByDeviceIdAndTag(deviceId, tag);

        if(data!=null){
            data.setValue(value);
            lastStoredDataRepository.save(data);
        }else{
            LastStoredData newData = new LastStoredData();
            newData.setValue(value);
            newData.setTag(tag);
            newData.setDeviceId(deviceId);
            lastStoredDataRepository.save(newData);
        }
    }
}
