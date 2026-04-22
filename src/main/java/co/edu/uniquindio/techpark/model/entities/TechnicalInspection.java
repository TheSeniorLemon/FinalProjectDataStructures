package co.edu.uniquindio.techpark.model.entities;

import java.util.Objects;

public class TechnicalInspection {
    private String id;
    private String dateTime;
    private String operatorId;
    private String operatorName;
    private String result;
    private String observations;
    private boolean successful;

    public TechnicalInspection(String id, String dateTime, String operatorId, String operatorName, String result, String observations, boolean successful) {
        this.id = id;
        this.dateTime = dateTime;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.result = result;
        this.observations = observations;
        this.successful = successful;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }

    public boolean isSuccessful() {
        return successful;
    }
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TechnicalInspection that = (TechnicalInspection) o;
        return successful == that.successful && Objects.equals(id, that.id) && Objects.equals(dateTime, that.dateTime) && Objects.equals(operatorId, that.operatorId) && Objects.equals(operatorName, that.operatorName) && Objects.equals(result, that.result) && Objects.equals(observations, that.observations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTime, operatorId, operatorName, result, observations, successful);
    }

    @Override
    public String toString() {
        return "TechnicalInspection{" +
                "id='" + id + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", result='" + result + '\'' +
                ", observations='" + observations + '\'' +
                ", successful=" + successful +
                '}';
    }

    public String getSummary() {
        return "[" + dateTime + "]" +
                " Operator: " + operatorName +
                " | Result: " + result +
                " | Successful: " + (successful ? "YES" : "NO") +
                (observations != null && !observations.isEmpty()
                        ? " | Obs: " + observations
                        : "");
    }
}