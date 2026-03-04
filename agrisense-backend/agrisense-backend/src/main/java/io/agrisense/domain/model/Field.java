package io.agrisense.domain.model;

public class Field {
    private Long id;
    private String name;
    private String location;
    private Long farmerId;
    
    public Field() {
    }
    
    public Field(String name, String location, Long farmerId) {
        this.name = name;
        this.location = location;
        this.farmerId = farmerId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Long getFarmerId() {
        return farmerId;
    }
    
    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }
}
