package gr.odys.ds_backend.payload.response;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    private List<String> errors;

    public ValidationErrorResponse() {
        this.errors = new ArrayList<>();
    }

    public ValidationErrorResponse(String error) {
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public ValidationErrorResponse(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}