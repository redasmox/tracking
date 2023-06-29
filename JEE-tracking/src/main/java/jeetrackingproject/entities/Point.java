package jeetrackingproject.entities;

public class Point {
    private Long id;
    private Double longitude;
    private Double latitude;
    private Long planning_id;
    private String hour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getPlanning_id() {
        return planning_id;
    }

    public void setPlanning_id(Long planning_id) {
        this.planning_id = planning_id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
