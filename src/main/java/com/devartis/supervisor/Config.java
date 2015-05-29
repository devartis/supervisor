package com.devartis.supervisor;

public class Config {

    private boolean validateNameUniqueness = true;

    public boolean isValidateNameUniqueness() {
        return validateNameUniqueness;
    }

    public void setValidateNameUniqueness(boolean validateNameUniqueness) {
        this.validateNameUniqueness = validateNameUniqueness;
    }
}
