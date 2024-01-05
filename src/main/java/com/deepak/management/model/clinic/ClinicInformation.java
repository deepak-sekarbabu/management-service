package com.deepak.management.model.clinic;

import com.deepak.management.model.common.PhoneNumbers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@ToString
@Entity(name = "clinic_information")
@JsonAutoDetect
public class ClinicInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    @Hidden
    private Integer clinicId;

    @Column(name = "clinic_name")
    @Schema(description = "Clinic Name", example = "Sample Clinic")
    @Length(max = 150)
    private String clinicName;

    @Column(name = "clinic_address")
    @Length(max = 200)
    @Schema(description = "Clinic Address", example = "Sample Address")
    private String clinicAddress;

    @Column(name = "map_geo_location")
    @Schema(description = "Clinic GeoLocation", example = "40.7128,-74.006")
    @Length(max = 50)
    private String mapGeoLocation;

    @Column(name = "clinic_pin_code")
    @Schema(description = "Clinic PinCode", example = "100000")
    @Length(max = 10)
    private String clinicPinCode;


    @Schema(description = "Clinic PhoneNumber", example = """
                                                          [
                                                                  {
                                                                      "phoneNumber": "123-456-7890"
                                                                  },
                                                                  {
                                                                      "phoneNumber": "987-654-3210"
                                                                  }
                                                              ]""")
    @Column(name = "clinic_phone_numbers")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<PhoneNumbers> clinicPhoneNumbers;

    @Min(message = "Clinic Cannot be created without single Doctor", value = 1)
    @Column(name = "no_of_doctors")
    @Max(message = "We support only 100 Doctors at the moment", value = 100)
    @Schema(description = "No of Doctors in Clinic", example = "4")
    private Integer noOfDoctors;

    @Column(name = "clinic_email", length = 120)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Email Id of Clinic", example = "clinic@clinic.com")
    private String clinicEmail;

    @Column(name = "clinic_timing", length = 150)
    @Schema(description = "Timings of the Clinic", example = "MON - FRI  10:00 - 12:00, SAT & SUN 18:00 - 21:00")
    private String clinicTimings;

    @Column(name = "clinic_website", length = 150)
    @Schema(description = "Website of the Clinic", example = "https://drmeenakshiclinic.com")
    private String clinicWebsite;

    @Column(name = "clinic_amenities", length = 200)
    @Schema(description = "Amenities of the Clinic", example = "Dental Surgery, Pharmacy , Free Wifi ,Televisions")
    private String clinicAmenities;

}