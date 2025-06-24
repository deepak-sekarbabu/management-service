package com.deepak.management.model.patient.converter;

import com.deepak.management.model.patient.PersonalDetails;

public class PersonalDetailsConverter extends JsonConverter<PersonalDetails> {
  public PersonalDetailsConverter() {
    super(PersonalDetails.class);
  }
}
