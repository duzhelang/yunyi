package com.oda.springboot.controller.dto;

public class SaveAndPredictRequest {
    private Integer pregnancies;
    private Double glucose;
    private Integer bloodPressure;
    private Integer skinThickness;
    private Double insulin;
    private Double bmi;
    private Double diabetesPedigreeFunction;
    private Integer age;
    private Double height;
    private Double weight;
    private String symptoms;
    private String exerciseFrequency;
    private String dietHabit;
    private String smoking;
    private String drinking;
    private String gender;
    private Boolean askAI;

    public Integer getPregnancies() { return pregnancies; }
    public void setPregnancies(Integer pregnancies) { this.pregnancies = pregnancies; }
    public Double getGlucose() { return glucose; }
    public void setGlucose(Double glucose) { this.glucose = glucose; }
    public Integer getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(Integer bloodPressure) { this.bloodPressure = bloodPressure; }
    public Integer getSkinThickness() { return skinThickness; }
    public void setSkinThickness(Integer skinThickness) { this.skinThickness = skinThickness; }
    public Double getInsulin() { return insulin; }
    public void setInsulin(Double insulin) { this.insulin = insulin; }
    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
    public Double getDiabetesPedigreeFunction() { return diabetesPedigreeFunction; }
    public void setDiabetesPedigreeFunction(Double diabetesPedigreeFunction) { this.diabetesPedigreeFunction = diabetesPedigreeFunction; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getExerciseFrequency() { return exerciseFrequency; }
    public void setExerciseFrequency(String exerciseFrequency) { this.exerciseFrequency = exerciseFrequency; }
    public String getDietHabit() { return dietHabit; }
    public void setDietHabit(String dietHabit) { this.dietHabit = dietHabit; }
    public String getSmoking() { return smoking; }
    public void setSmoking(String smoking) { this.smoking = smoking; }
    public String getDrinking() { return drinking; }
    public void setDrinking(String drinking) { this.drinking = drinking; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Boolean getAskAI() { return askAI; }
    public void setAskAI(Boolean askAI) { this.askAI = askAI; }
}
