package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import static de.symeda.sormas.api.dashboard.EpiCurveGrouping.DAY;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;

public class ContactClassificationCurveBuilder extends ContactsEpiCurveBuilder {

	public ContactClassificationCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	public void buildEpiCurve(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
		int[] unconfirmedNumbers = new int[datesGroupedBy.size()];
		int[] confirmedNumbers = new int[datesGroupedBy.size()];

		for (int i = 0; i < datesGroupedBy.size(); i++) {
			Date date = datesGroupedBy.get(i);

			ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardDataProvider.getDisease())
				.region(dashboardDataProvider.getRegion())
				.district(dashboardDataProvider.getDistrict());
			if (epiCurveGrouping == DAY) {
				contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
			} else {
				contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
			}

			Map<ContactClassification, Long> contactCounts = FacadeProvider.getContactFacade().getNewContactCountPerClassification(contactCriteria);

			Long unconfirmedCount = contactCounts.get(ContactClassification.UNCONFIRMED);
			Long confirmedCount = contactCounts.get(ContactClassification.CONFIRMED);
			unconfirmedNumbers[i] = unconfirmedCount != null ? unconfirmedCount.intValue() : 0;
			confirmedNumbers[i] = confirmedCount != null ? confirmedCount.intValue() : 0;
		}

		hcjs.append("series: [");
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardUnconfirmedContact)
				+ "', color: '#808080', dataLabels: { allowOverlap: false }, data: [");
		for (int i = 0; i < unconfirmedNumbers.length; i++) {
			if (i == unconfirmedNumbers.length - 1) {
				hcjs.append(unconfirmedNumbers[i] + "]},");
			} else {
				hcjs.append(unconfirmedNumbers[i] + ", ");
			}
		}
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardConfirmedContact)
				+ "', color: '#005A9C', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < confirmedNumbers.length; i++) {
			if (i == confirmedNumbers.length - 1) {
				hcjs.append(confirmedNumbers[i] + "]}],");
			} else {
				hcjs.append(confirmedNumbers[i] + ", ");
			}
		}
	}
}
