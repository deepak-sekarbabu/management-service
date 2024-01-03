package com.deepak.management.model;

import com.deepak.management.utils.JsonListConverter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Clinic")
@JsonAutoDetect
public class ClinicInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Integer clinicId;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "clinic_address")
    private String clinicAddress;

    @Column(name = "map_geo_location")
    private String mapGeoLocation;

    @Column(name = "clinic_pin_code")
    private String clinicPinCode;

    @Convert(converter = JsonListConverter.class)
    @Column(columnDefinition = "json")
    private List<String> clinicPhoneNumbers;

    @Column(name = "no_of_doctors")
    private Integer noOfDoctors;

}