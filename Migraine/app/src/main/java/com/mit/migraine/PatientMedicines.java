package com.mit.migraine;

/**
 * Created by cs on 8/21/2017.
 */

public class PatientMedicines {
    String med_name;
    String med_strength;
    String med_quantity;

    public PatientMedicines() {}

    public PatientMedicines(String med_name, String med_strength, String med_quantitily) {
        this.med_name = med_name;
        this.med_strength = med_strength;
        this.med_quantity = med_quantitily;
    }

    public String getMed_name() {
        return med_name;
    }

    public void setMed_name(String med_name) {
        this.med_name = med_name;
    }

    public String getMed_strength() {
        return med_strength;
    }

    public void setMed_strength(String med_strength) {
        this.med_strength = med_strength;
    }

    public String getMed_quantity() {
        return med_quantity;
    }

    public void setMed_quantitily(String med_quantity) {
        this.med_quantity = med_quantity;
    }
}
