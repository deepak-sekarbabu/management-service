package com.deepak.management.model.patient;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordRequest {
  private String newPassword;

  @Override
  public String toString() {
    return "UpdatePasswordRequest(newPassword='[MASKED]')";
  }
}
