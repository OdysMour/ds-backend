package gr.odys.ds_backend.payload.response;

import gr.odys.ds_backend.entity.Citizen;

public class CitizenDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Long phone;
    private String city;

    public CitizenDTO() {} // Default constructor for serialization

    public CitizenDTO(Citizen citizen) {
        this.id = citizen.getId();
        this.firstName = citizen.getFirstName();
        this.lastName = citizen.getLastName();
        this.phone = citizen.getPhone();
        this.city = citizen.getCity();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }
}