package com.deepak.management.model.patient.converter;

import com.deepak.management.model.patient.EmergencyContact;

public class EmergencyContactConverter extends JsonConverter<EmergencyContact> {
  public EmergencyContactConverter() {
    super(EmergencyContact.class);
  }
}
