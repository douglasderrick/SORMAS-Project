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

package org.sormas.e2etests.pages.application.dashboard.Surveillance;

import org.openqa.selenium.By;

public class ContactsDashboardPage {

  public static final By CONTACTS_RADIO_BUTTON = By.xpath("//label[contains(text(),'Contacts')]");
  public static final By REGION_FILTER_COMBOBOX_CONTACTS_DASHBOARD =
      By.cssSelector("[location='diseaseFilter']>div>div");
  public static final By DISEASE_FILTER_COMBOBOX_CONTACTS_DASHBOARD =
      By.cssSelector("[location='diseaseFilter']>div>div");
  public static final By RESET_FILTERS_BUTTON_CONTACTS_DASHBOARD = By.id("actionResetFilters");
  public static final By APPLY_FILTERS_BUTTON_CONTACTS_DASHBOARD = By.id("actionApplyFilters");
  public static final By SHOW_ALL_DISEASES_BUTTON_CONTACTS_DASHBOARD =
      By.id("dashboardShowAllDiseases");
  public static final By SHOW_FIRST_DISEASES_BUTTON_CONTACTS_DASHBOARD =
      By.id("dashboardShowFirstDiseases");
  public static final By CONTACTS_DASHBOARD_NAME =
      By.xpath("//div[contains(text(),'Contacts Dashboard')]");
  public static final By ALL_CONTACTS_COUNTER =
      By.xpath("//div[text()='All Contacts']/parent::div/following-sibling::div//child::div");
  public static final By UNDER_FOLLOWUP_COUNTER =
      By.cssSelector("//div[text()='Under Follow-up']/parent::div/following-sibling::div//child::div");
  public static final By STOPPED_FOLLOWUP_COUNTER =
      By.cssSelector("//div[text()='Stopped Follow-up']/parent::div/following-sibling::div//child::div");
  public static final By VISITS_COUNTER =
      By.cssSelector("//div[text()='Visits']/parent::div/following-sibling::div//child::div");
  public static final By UNCONFIRMED_CONTACT_COUNTER =
      By.cssSelector("//div[text()='Unconfirmed contact']/parent::div/preceding-sibling::div");
  public static final By CONFIRMED_CONTACT_COUNTER =
      By.cssSelector("//div[text()='Confirmed contact']/parent::div/preceding-sibling::div");
  public static final By NOT_A_CONTACT_COUNTER =
      By.cssSelector("//div[text()='Not a contact']/parent::div/preceding-sibling::div");
  public static final By NEW_COUNTER =
      By.cssSelector("//div[text()='New']/parent::div/preceding-sibling::div");
  public static final By SYMPTOMATIC_COUNTER =
      By.cssSelector("//div[text()='Symptomatic']/parent::div/preceding-sibling::div");
  public static final By COOPERATIVE_COUNTER =
      By.cssSelector("//div[text()='Cooperative']/parent::div/preceding-sibling::div");
  public static final By UNCOOPERATIVE_COUNTER =
      By.cssSelector("//div[text()='Uncooperative']/parent::div/preceding-sibling::div");
  public static final By UNAVAILABLE_COUNTER =
      By.cssSelector("//div[text()='Unavailable']/parent::div/preceding-sibling::div");
  public static final By NEVER_VISITED_COUNTER =
      By.cssSelector("//div[text()='Never visited']/parent::div/preceding-sibling::div");
  public static final By COMPLETED_FOLLOW_UP_COUNTER =
      By.cssSelector("//div[text()='Completed follow-up']/parent::div/following-sibling::div[2]");
  public static final By CANCELLED_FOLLOW_UP_COUNTER =
      By.cssSelector("//div[text()='Canceled follow-up']/parent::div/following-sibling::div[2]");
  public static final By LOST_TO_FOLLOW_UP_COUNTER =
      By.cssSelector("//div[text()='Lost to follow-up']/parent::div/following-sibling::div[2]");
  public static final By CONVERTED_TO_CASE_COUNTER =
      By.cssSelector("//div[text()='Converted to case']/parent::div/following-sibling::div[2]");
  public static final By UNAVAILABLE_VISITS_COUNTER =
      By.cssSelector("//div[text()='Unavailable']/parent::div/following-sibling::div[1]");
  public static final By UNCOOPERATIVE_VISITS_COUNTER =
      By.cssSelector("//div[text()='Uncooperative']/parent::div/following-sibling::div[1]");
  public static final By COOPERATIVE_VISITS_COUNTER =
      By.cssSelector("//div[text()='Cooperative']/parent::div/following-sibling::div[1]");
  public static final By MISSED_VISITS_COUNTER =
      By.cssSelector("//div[text()='Missed']/parent::div/following-sibling::div[1]");
  public static final By CONTACTS_COVID19_COUNTER =
      By.xpath("//div[contains(text(),'COVID-19')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_CHOLERA_COUNTER =
      By.xpath("//div[contains(text(),'Cholera')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_CONGENITAL_RUBELLA_COUNTER =
      By.xpath("//div[contains(text(),'Congenital Rubella')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_EBOLA_VIRUS_DISEASE_COUNTER =
      By.xpath("//div[contains(text(),'Ebola Virus Disease')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_LASSA_COUNTER =
      By.xpath("//div[contains(text(),'Lassa')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_MONKEYPOX_COUNTER =
      By.xpath("//div[contains(text(),'Monkeypox')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_INFLUENZA_NEW_SUBTYPE_COUNTER =
      By.xpath("//div[contains(text(),'Influenza (New subtype)')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_PLAGUE_COUNTER =
      By.xpath("//div[contains(text(),'Plague')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_UNSPECIFIED_WHF_COUNTER =
      By.xpath("//div[contains(text(),'Unspecified VHF')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_HUMAN_RABIES_COUNTER =
      By.xpath("//div[contains(text(),'Human Rabies')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_OTHER_EPIDEMIC_DISEASE_COUNTER =
      By.xpath("//div[contains(text(),'Other Epidemic Disease')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_NOT_YET_DEFINED_COUNTER =
      By.xpath("//div[contains(text(),'Not Yet Defined')]/parent::div/following-sibling::div[4]/div");
  public static final By CONTACTS_PER_CASE_MIN_COUNTER =
      By.xpath("//div[contains(text(),'Contacts per Case')]/parent::div/following-sibling::div[1]");
  public static final By CONTACTS_PER_CASE_MAX_COUNTER =
      By.xpath("//div[contains(text(),'Contacts per Case')]/parent::div/following-sibling::div[2]");
  public static final By CONTACTS_PER_CASE_AVERAGE_COUNTER =
      By.xpath("//div[contains(text(),'Contacts per Case')]/parent::div/following-sibling::div[3]");
  public static final By CONTACTS_IN_QUARANTINE_COUNTER =
      By.xpath("//div[contains(text(),'Contacts in Quarantine')]/parent::div/following-sibling::div");
  public static final By CONTACTS_PLACED_IN_QUARANTINE_COUNTER =
      By.xpath("//div[contains(text(),'Contacts placed in Quarantine')]/parent::div/following-sibling::div");
  public static final By CONTACTS_NEW_CASES_NOT_PREVIOUSLY_KNOWN_TO_BE_CONTACTS_COUNTER =
      By.xpath("//div[contains(text(),'New Cases not Previously Known to Be Contacts')]/parent::div/following-sibling::div");
}
