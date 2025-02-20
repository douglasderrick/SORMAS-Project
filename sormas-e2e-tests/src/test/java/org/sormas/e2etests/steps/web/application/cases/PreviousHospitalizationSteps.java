package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.FIRST_PREVIOUS_HOSPITALIZATION_ENTRY;
import static org.sormas.e2etests.pages.application.cases.HospitalizationTabPage.NEW_ENTRY_LINK;
import static org.sormas.e2etests.pages.application.cases.PreviousHospitalizationPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Hospitalization;
import org.sormas.e2etests.entities.pojo.web.PreviousHospitalization;
import org.sormas.e2etests.entities.services.HospitalizationService;
import org.sormas.e2etests.entities.services.PreviousHospitalizationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class PreviousHospitalizationSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static PreviousHospitalization previousHospitalization;
  private static Hospitalization createdHospitalization;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public PreviousHospitalizationSteps(
      WebDriverHelpers webDriverHelpers,
      PreviousHospitalizationService previousHospitalizationService,
      HospitalizationService hospitalizationService,
      SoftAssert softly) {

    this.webDriverHelpers = webDriverHelpers;

    When(
        "I add a previous hospitalization and save",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_LINK);
          previousHospitalization =
              previousHospitalizationService.generatePreviousHospitalization();
          fillDateOfVisitOrAdmission(previousHospitalization.getDateOfVisitOrAdmission());
          selectReasonForHospitalization(previousHospitalization.getReasonForHospitalization());
          fillDateOfDischargeOrTransfer(previousHospitalization.getDateOfDischargeOrTransfer());
          selectStayInTheIntensiveCareUnit(previousHospitalization.getStayInTheIntensiveCareUnit());
          selectRegion(previousHospitalization.getRegion());
          selectDistrict(previousHospitalization.getDistrict());
          selectCommunity(previousHospitalization.getCommunity());
          selectHospital(previousHospitalization.getHospital());
          fillStartOfStayDate(previousHospitalization.getStartOfStayDate());
          selectIsolation(previousHospitalization.getIsolation());
          fillDateOfIsolation(previousHospitalization.getDateOfIsolation());
          fillEndOfStayDate(previousHospitalization.getEndOfStayDate());
          fillSpecifyReason(previousHospitalization.getSpecifyReason());
          fillDescription(previousHospitalization.getDescription());
          fillFacilityNameDescription(previousHospitalization.getFacilityNameDescription());
          selectWasPatientAdmittedAtTheFacilityAsAnInpatient(
              previousHospitalization.getIsolation());
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_ENTRY_LINK);
        });

    When(
        "I check the edited and saved data is correctly displayed in previous hospitalization window",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_PREVIOUS_HOSPITALIZATION_ENTRY);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(REGION_COMBOBOX);
          PreviousHospitalization collectedHospitalization = collectPreviousHospitalizationData();
          ComparisonHelper.compareEqualEntities(collectedHospitalization, previousHospitalization);
        });

    And(
        "^I check if Previous hospitalization Popup contains additional fields$",
        () -> {
          webDriverHelpers.isElementVisibleWithTimeout(ADMITTED_AS_INPATIENT, 1);
          webDriverHelpers.isElementVisibleWithTimeout(DATE_OF_ISOLATION, 1);
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON);
        });

    And(
        "I set Isolation as {string}",
        (String isolationOption) ->
            webDriverHelpers.clickWebElementByText(ISOLATION_OPTIONS, isolationOption));

    Then(
        "^I check the edited and saved current hospitalization is correctly displayed in previous hospitalization window$",
        () -> {
          createdHospitalization = hospitalizationService.generateHospitalization();
          webDriverHelpers.clickOnWebElementBySelector(FIRST_PREVIOUS_HOSPITALIZATION_ENTRY);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(REGION_COMBOBOX);
          PreviousHospitalization collectedHospitalization = collectPreviousHospitalizationData();
          softly.assertEquals(
              collectedHospitalization.getDateOfVisitOrAdmission(),
              createdHospitalization.getDateOfVisitOrAdmission(),
              "Date of visit or admission is not correct");

          softly.assertEquals(
              collectedHospitalization.getDateOfDischargeOrTransfer(),
              createdHospitalization.getDateOfDischargeOrTransfer(),
              "Date of discharge or transfer is not correct");

          softly.assertEquals(
              collectedHospitalization.getReasonForHospitalization(),
              createdHospitalization.getReasonForHospitalization(),
              "Reason for hospitalization is not correct");

          softly.assertEquals(
              collectedHospitalization.getSpecifyReason(),
              createdHospitalization.getSpecifyReason(),
              "Specify reason is not correct");

          softly.assertEquals(
              collectedHospitalization.getStayInTheIntensiveCareUnit(),
              createdHospitalization.getStayInTheIntensiveCareUnit(),
              "Stay in the intensive care unit is not correct");

          softly.assertEquals(
              collectedHospitalization.getStartOfStayDate(),
              createdHospitalization.getStartOfStayDate(),
              "Start of stay date is not correct");

          softly.assertEquals(
              collectedHospitalization.getEndOfStayDate(),
              createdHospitalization.getEndOfStayDate(),
              "End of stay date is not correct");

          softly.assertEquals(
              collectedHospitalization.getIsolation(),
              createdHospitalization.getIsolation(),
              "Isolation is not correct");

          softly.assertEquals(
              collectedHospitalization.getDateOfIsolation(),
              createdHospitalization.getDateOfIsolation(),
              "Date of isolation is not correct");

          softly.assertEquals(
              collectedHospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient(),
              createdHospitalization.getWasPatientAdmittedAtTheFacilityAsAnInpatient(),
              "Was patient admitted at the facility as an inpatient is not correct");

          softly.assertEquals(
              collectedHospitalization.getDescription(),
              createdHospitalization.getDescription(),
              "Description is not correct");

          softly.assertAll();
        });
  }

  private void fillDateOfVisitOrAdmission(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_VISIT_OR_ADMISSION_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillDateOfDischargeOrTransfer(LocalDate date) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_DISCHARGE_OR_TRANSFER_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectReasonForHospitalization(String reason) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_HOSPITALIZATION_COMBOBOX, reason);
  }

  private void fillSpecifyReason(String text) {
    webDriverHelpers.fillInWebElement(SPECIFY_REASON_INPUT, text);
  }

  private void selectStayInTheIntensiveCareUnit(String option) {
    webDriverHelpers.clickWebElementByText(STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS, option);
  }

  private void fillStartOfStayDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void fillEndOfStayDate(LocalDate date) {
    webDriverHelpers.fillInWebElement(END_OF_STAY_DATE_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectIsolation(String option) {
    webDriverHelpers.clickWebElementByText(ISOLATION_OPTIONS, option);
  }

  private void fillDateOfIsolation(LocalDate date) {
    webDriverHelpers.fillInWebElement(DATE_OF_ISOLATION, DATE_FORMATTER.format(date));
  }

  private void selectRegion(String option) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, option);
  }

  private void selectDistrict(String option) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, option);
  }

  private void selectCommunity(String option) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, option);
  }

  private void selectHospital(String option) {
    webDriverHelpers.selectFromCombobox(HOSPITAL_COMBOBOX, option);
  }

  private void fillDescription(String text) {
    webDriverHelpers.fillInWebElement(DESCRIPTION_INPUT, text);
  }

  private void fillFacilityNameDescription(String text) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_DESCRIPTION_INPUT, text);
  }

  private void selectWasPatientAdmittedAtTheFacilityAsAnInpatient(String option) {
    webDriverHelpers.clickWebElementByText(WAS_THE_PATIENT_ADMITTED_AS_INPATIENT_OPTIONS, option);
  }

  private PreviousHospitalization collectPreviousHospitalizationData() {
    return PreviousHospitalization.builder()
        .dateOfVisitOrAdmission(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_VISIT_OR_ADMISSION_INPUT),
                DATE_FORMATTER))
        .dateOfDischargeOrTransfer(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_DISCHARGE_OR_TRANSFER_INPUT),
                DATE_FORMATTER))
        .reasonForHospitalization(
            webDriverHelpers.getValueFromCombobox(REASON_FOR_HOSPITALIZATION_COMBOBOX))
        .specifyReason(webDriverHelpers.getValueFromWebElement(SPECIFY_REASON_INPUT))
        .stayInTheIntensiveCareUnit(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS))
        .startOfStayDate(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_STAY_DATE_INPUT), DATE_FORMATTER))
        .endOfStayDate(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_STAY_DATE_INPUT), DATE_FORMATTER))
        .isolation(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ISOLATION_OPTIONS))
        .dateOfIsolation(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(DATE_OF_ISOLATION), DATE_FORMATTER))
        .description(webDriverHelpers.getValueFromWebElement(DESCRIPTION_INPUT))
        .facilityNameDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_INPUT))
        .region(webDriverHelpers.getValueFromCombobox(REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .hospital(webDriverHelpers.getValueFromCombobox(HOSPITAL_COMBOBOX))
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                WAS_THE_PATIENT_ADMITTED_AS_INPATIENT_OPTIONS))
        .build();
  }
}
