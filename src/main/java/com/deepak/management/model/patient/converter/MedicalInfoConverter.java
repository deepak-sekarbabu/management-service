package com.deepak.management.model.patient.converter;

import com.deepak.management.model.patient.MedicalInfo;

public class MedicalInfoConverter extends JsonConverter<MedicalInfo> {
  public MedicalInfoConverter() {
    super(MedicalInfo.class);
  }
}
