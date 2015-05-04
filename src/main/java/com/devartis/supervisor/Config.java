package com.devartis.supervisor;

/**
 * Created by german on 4/22/15.
 */
public class Config {

    private boolean validateNameUniqueness = true;

    public boolean isValidateNameUniqueness() {
        return validateNameUniqueness;
    }

    public void setValidateNameUniqueness(boolean validateNameUniqueness) {
        this.validateNameUniqueness = validateNameUniqueness;
    }
}
