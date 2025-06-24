package com.deepak.management.model.patient.converter;

import com.deepak.management.model.patient.InsuranceDetails;

public class InsuranceDetailsConverter extends JsonConverter<InsuranceDetails> {
  public InsuranceDetailsConverter() {
    super(InsuranceDetails.class);
  }
}
