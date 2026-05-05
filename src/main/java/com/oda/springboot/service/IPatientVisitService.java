package com.oda.springboot.service;

import com.oda.springboot.entity.PatientVisitRecord;

import java.util.List;
import java.util.Map;

public interface IPatientVisitService {

    PatientVisitRecord add(PatientVisitRecord record);

    PatientVisitRecord getById(Integer id);

    List<PatientVisitRecord> getMyRecords(Integer userId, Integer limit);

    List<PatientVisitRecord> getChartData(Integer userId);

    void update(PatientVisitRecord record);

    void delete(Integer id, Integer userId);

    void cleanupOldRecords(Integer userId);
}
