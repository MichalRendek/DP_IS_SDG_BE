package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.DataPointTagRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.DataPointTagService;
import sk.stuba.sdg.isbe.utilities.StoredDataMergeResponse;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataPointTagServiceImpl implements DataPointTagService {

    @Autowired
    private DataPointTagRepository dataPointTagRepository;

    @Autowired
    private StoredDataRepository storedDataRepository;

    @Override
    public DataPointTag createDataPointTag(DataPointTag dataPointTag){
        if (!dataPointTag.isValid()) {
            throw new InvalidEntityException("Data Point Tag has no name or unit set!");
        }

        dataPointTag.setCreatedAt(Instant.now().toEpochMilli());
        return dataPointTagRepository.save(dataPointTag);
    }

    @Override
    public List<DataPointTag> getDataPointTags() {
        return dataPointTagRepository.findAll();
    }

    @Override
    public DataPointTag getDataPointTagById(String dataPointTagId) {
        if (dataPointTagId == null || dataPointTagId.isEmpty()) {
            throw new InvalidEntityException("Data Point Tag id is not set!");
        }

        return dataPointTagRepository.getDataPointTagByUid(dataPointTagId);
    }

    @Override
    public List<StoredData> getStoredData(String dataPointTagId) {
        if (dataPointTagId == null || dataPointTagId.isEmpty()) {
            throw new InvalidEntityException("Data Point Tag id is not set!");
        }

        return getDataPointTagById(dataPointTagId).getStoredData();
    }

    @Override
    public List<StoredDataMergeResponse> getStoredDataForTags(List<String> dataPointTagIds, Long startTime, Long endTime,  Long cadence, int method) {
        // This map will hold the intermediate results with measureAtDevice as key
        Map<Double, Map<String, StoredData>> groupedData = new HashMap<>();

        // Assuming getDataPointTagById(dataPointTagId) fetches a list of StoredData for a given ID
        // You need to implement the actual fetching logic as per your application's structure
        for(String dataPointTagId : dataPointTagIds) {
            List<StoredData> storedDatas = getStoredDataByTime(dataPointTagId, startTime, endTime, cadence, method); // This method needs to be defined

            for (StoredData storedData : storedDatas) {
                groupedData.computeIfAbsent(storedData.getMeasureAtDevice(), k -> new HashMap<>())
                        .put(storedData.getTag(), storedData);
            }
        }

        // Transform the grouped data into a list of StoredDataMergeResponse
        List<StoredDataMergeResponse> responses = new ArrayList<>();
        for (Map.Entry<Double, Map<String, StoredData>> entry : groupedData.entrySet()) {
            StoredDataMergeResponse response = new StoredDataMergeResponse();
            response.setMeasureAtDevice(entry.getKey());
            response.setTagValues(entry.getValue());
            responses.add(response);
        }

        return responses;
    }

    @Override
    public List<StoredData> getStoredDataByTime(String dataPointTagId, Long startTime, Long endTime,  Long cadence, int method){
        if (dataPointTagId == null || dataPointTagId.isEmpty()) {
            throw new InvalidEntityException("Data Point Tag id is not set!");
        }

        startTime -= 1;
        endTime += 1;

        if (endTime < startTime) {
            throw new InvalidEntityException("Start is bigger then end!");
        }

        List<StoredData> storedData = storedDataRepository.findStoredDataByDataPointTagIdAndMeasureAtBetween(dataPointTagId,startTime, endTime);

        List<StoredData> aggregatedData = new ArrayList<>();

        int totalItems = storedData.size();
        Long itemsPerGroup = Math.max(1, totalItems / cadence);

        for (int i = 0; i < totalItems; i += itemsPerGroup) {
            Long end = Math.min(i + itemsPerGroup, totalItems);
            List<Double> values = storedData.subList(i, Math.toIntExact(end)).stream().map(StoredData::getValue).collect(Collectors.toList());

            // Calculate the statistic based on the selected method
            double statisticValue = calculateStatistic(values, method);


            StoredData averageData = storedData.get(i);
            averageData.setValue(statisticValue);
            aggregatedData.add(averageData);
        }

        return aggregatedData;

    }

    private double calculateStatistic(List<Double> values, int method) {
        switch (method) {
            case 1: // AVERAGE Calculates the mean of the data points
                return values.stream().mapToDouble(v -> v).average().orElse(0);
            case 2: // MEDIAN Finds the middle value of the data points
                Collections.sort(values);
                int middle = values.size() / 2;
                if (values.size() % 2 == 0) {
                    return (values.get(middle - 1) + values.get(middle)) / 2.0;
                } else {
                    return values.get(middle);
                }
            case 3: // MODE Finds the most frequently occurring value
                return values.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0.0);
            case 4: // MIN Finds the minimum value
                return Collections.min(values);
            case 5: // MAX Finds the maximum value
                return Collections.max(values);
            case 6: // STANDARD_DEVIATION Calculates the standard deviation of the data points
                double mean = values.stream().mapToDouble(v -> v).average().orElse(0);
                double temp = 0;
                for (double a : values)
                    temp += (mean - a) * (mean - a);
                return Math.sqrt(temp / values.size());
            case 7: // VARIANCE Calculates the variance of the data points
                double meanVariance = values.stream().mapToDouble(v -> v).average().orElse(0);
                double varianceTemp = 0;
                for (double a : values)
                    varianceTemp += (meanVariance - a) * (meanVariance - a);
                return varianceTemp / values.size();
            case 8: // RANGE Calculates the range (difference between max and min) of the data points
                return Collections.max(values) - Collections.min(values);
            case 9: // SUM Calculates the sum of all data points
                return values.stream().mapToDouble(Double::doubleValue).sum();
            case 10: // COUNT Counts the number of data points
                return values.size();
            case 11: // GEOMETRIC_MEAN Calculates the geometric mean of the data points
                double product = values.stream().reduce(1.0, (a, b) -> a * b);
                return Math.pow(product, 1.0 / values.size());
            case 12: // HARMONIC_MEAN Calculates the harmonic mean of the data points
                double sumInverse = values.stream().reduce(0.0, (a, b) -> a + 1.0 / b);
                return values.size() / sumInverse;
            default:
                throw new IllegalArgumentException("Unknown statistical method");
        }
    }

    @Override
    public DataPointTag updateDataPointTag(String dataPointTagId, DataPointTag changeDataPointTag) {
        DataPointTag dataPointTag = getDataPointTagById(dataPointTagId);

        if (changeDataPointTag == null) {
            throw new InvalidEntityException("DataPointTag with changes is null!");
        }

        if (changeDataPointTag.getName() != null) {
            dataPointTag.setName(changeDataPointTag.getName());
        }
        if (changeDataPointTag.getUnit() != null) {
            dataPointTag.setUnit(changeDataPointTag.getUnit());
        }
        if (changeDataPointTag.getDecimal() != null){
            dataPointTag.setDecimal(changeDataPointTag.getDecimal());
        }
        if (changeDataPointTag.getTag() != null){
            dataPointTag.setTag(changeDataPointTag.getTag());
        }

        /**
         * TODO - update vsetkych StoredData, ktore maju v sebe tento DataPointTag
         * dôvod: odstranil sa @DBRef na DataPointTag v triede StoredData
         */

        return dataPointTagRepository.save(dataPointTag);
    }

    @Override
    public DataPointTag deleteDataPointTag(String dataPointTagId) {
        DataPointTag dataPointTag = getDataPointTagById(dataPointTagId);
        dataPointTag.setDeactivated(true);

        /**
         * TODO - update vsetkych StoredData, ktore maju v sebe tento DataPointTag
         * dôvod: odstranil sa @DBRef na DataPointTag v triede StoredData
         */

        dataPointTagRepository.save(dataPointTag);
        return dataPointTag;
    }
}
