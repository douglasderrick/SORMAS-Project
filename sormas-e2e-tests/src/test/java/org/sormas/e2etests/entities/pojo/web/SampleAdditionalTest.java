/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class SampleAdditionalTest {
  LocalDate dateOfResult;
  LocalTime timeOfResult;
  String haemoglobinInUrine;
  String proteinInUrine;
  String redBloodCellsInUrine;
  String ph;
  String pCO2;
  String pAO2;
  String hCO3;
  String oxygen;
  String sgpt;
  String totalBilirubin;
  String sgot;
  String conjBilirubin;
  String creatine;
  String wbc;
  String potassium;
  String platelets;
  String urea;
  String prothrombin;
  String haemoglobin;
  String otherResults;
}
