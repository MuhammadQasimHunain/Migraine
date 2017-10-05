package com.mit.migraine;

import java.io.Serializable;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs on 8/17/2017.
 */

public class PersonalInformation implements Serializable {
    private String age;
    private String gender;
    private String methodsOfBirthControl;
    private String dateOfLMP;
    private String anticipatedDataOfNextPeriod;
    private List<String> otherMedicalConditions;
    private List<PatientMedicines> listOfMedications;
    private String headacheLostFor;
    private List<String> headacheTypes;
    private List<String> associatedSymptoms;
    private List<String> headacheLocations;
    private List<String> triggers;
    private List<String> helpers;
    private String noOfPrompts;
    private String timeOfPrompt1;
    private String timeOfPrompt2;

    public PersonalInformation() {
        this.age = "";
        this.gender = "Male";
        this.methodsOfBirthControl = "";
        this.dateOfLMP = "";
        this.anticipatedDataOfNextPeriod = "";
        this.otherMedicalConditions = new ArrayList<>();
        this.listOfMedications = new ArrayList<>();
        this.headacheLostFor = "";
        this.headacheTypes = new ArrayList<>();
        this.associatedSymptoms = new ArrayList<>();
        this.headacheLocations = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.helpers = new ArrayList<>();
        this.noOfPrompts = "1";
        this.timeOfPrompt1 = "10:00 AM";
        this.timeOfPrompt2 = "10:00 PM";
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMethodsOfBirthControl() {
        return methodsOfBirthControl;
    }

    public void setMethodsOfBirthControl(String methodsOfBirthControl) {
        this.methodsOfBirthControl = methodsOfBirthControl;
    }

    public String getDateOfLMP() {
        return dateOfLMP;
    }

    public void setDateOfLMP(String dateOfLMP) {
        this.dateOfLMP = dateOfLMP;
    }

    public String getAnticipatedDataOfNextPeriod() {
        return anticipatedDataOfNextPeriod;
    }

    public void setAnticipatedDataOfNextPeriod(String anticipatedDataOfNextPeriod) {
        this.anticipatedDataOfNextPeriod = anticipatedDataOfNextPeriod;
    }

    public List<String> getOtherMedicalConditions() {
        return otherMedicalConditions;
    }

    public void setOtherMedicalConditions(List<String> otherMedicalConditions) {
        this.otherMedicalConditions = otherMedicalConditions;
    }

    public List<PatientMedicines> getListOfMedications() {
        return listOfMedications;
    }

    public void setListOfMedications(List<PatientMedicines> listOfMedications) {
        this.listOfMedications = listOfMedications;
    }

    public String getHeadacheLostFor() {
        return headacheLostFor;
    }

    public void setHeadacheLostFor(String headacheLostFor) {
        this.headacheLostFor = headacheLostFor;
    }

    public List<String> getHeadacheTypes() {
        return headacheTypes;
    }

    public void setHeadacheTypes(List<String> headacheTypes) {
        this.headacheTypes = headacheTypes;
    }

    public List<String> getAssociatedSymptoms() {
        return associatedSymptoms;
    }

    public void setAssociatedSymptoms(List<String> associatedSymptoms) {
        this.associatedSymptoms = associatedSymptoms;
    }

    public List<String> getHeadacheLocations() {
        return headacheLocations;
    }

    public void setHeadacheLocations(List<String> headacheLocations) {
        this.headacheLocations = headacheLocations;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getHelpers() {
        return helpers;
    }

    public void setHelpers(List<String> helpers) {
        this.helpers = helpers;
    }

    public String getNoOfPrompts() {
        return noOfPrompts;
    }

    public void setNoOfPrompts(String noOfPrompts) {
        this.noOfPrompts = noOfPrompts;
    }

    public String getTimeOfPrompt1() {
        return timeOfPrompt1;
    }

    public void setTimeOfPrompt1(String timeOfPrompt1) {
        this.timeOfPrompt1 = timeOfPrompt1;
    }

    public String getTimeOfPrompt2() {
        return timeOfPrompt2;
    }

    public void setTimeOfPrompt2(String timeOfPrompt2) {
        this.timeOfPrompt2 = timeOfPrompt2;
    }
}
