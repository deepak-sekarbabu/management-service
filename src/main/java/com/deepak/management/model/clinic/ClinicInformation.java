package com.deepak.management.model.clinic;

import com.deepak.management.model.common.PhoneNumbers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

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
  @Schema(
      description = "The official name of the clinic.",
      example = "Meenakshi Multi-Speciality Clinic")
  @Length(max = 150)
  private String clinicName;

  @Column(name = "clinic_address")
  @Length(max = 200)
  @Schema(
      description = "Full physical address of the clinic, including street, city, and state.",
      example = "No. 45, MG Road, Opposite City Mall, Bengaluru, Karnataka")
  private String clinicAddress;

  @Column(name = "map_geo_location")
  @Schema(
      description = "Geographical coordinates (latitude,longitude) of the clinic for map location.",
      example = "12.9716,77.5946")
  @Length(max = 50)
  private String mapGeoLocation;

  @Column(name = "clinic_pin_code")
  @Schema(description = "Postal pin code of the clinic's location.", example = "560001")
  @Length(max = 10)
  private String clinicPinCode;

  @Schema(
      description =
          "List of contact phone numbers for the clinic, each as an object with a phoneNumber field.",
      example =
          """
                [
                        {
                            \"phoneNumber\": \"+91-0802656789\"
                        },
                        {
                            \"phoneNumber\": \"+91-9876543210\"
                        }
                    ]""")
  @Column(name = "clinic_phone_numbers")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<PhoneNumbers> clinicPhoneNumbers;

  @Min(message = "Clinic Cannot be created without single Doctor", value = 1)
  @Column(name = "no_of_doctors")
  @Max(message = "We support only 100 Doctors at the moment", value = 100)
  @Schema(
      description = "Total number of doctors currently practicing at the clinic.",
      example = "2")
  private Integer noOfDoctors;

  @Column(name = "clinic_email", length = 120)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Email(message = "Please provide a valid email address")
  @Schema(
      description = "Primary email address for clinic contact and communication.",
      example = "info@meenakshiclinic.com")
  private String clinicEmail;

  @Column(name = "clinic_timing", length = 150)
  @Schema(
      description = "Operating hours of the clinic, including days and timings.",
      example = "MON - SAT 8:00 AM - 8:00 PM, SUN 9:00 AM - 1:00 PM")
  private String clinicTimings;

  @Column(name = "clinic_website", length = 150)
  @Schema(
      description = "Official website URL of the clinic.",
      example = "https://meenakshiclinic.com")
  private String clinicWebsite;

  @Column(name = "clinic_amenities", length = 200)
  @Schema(
      description = "Comma-separated list of amenities and services available at the clinic.",
      example =
          "General Medicine, Cardiology, Pediatrics, Pharmacy, AC Waiting Lounge, Free WiFi, Wheelchair Access")
  private String clinicAmenities;
}
