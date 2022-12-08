///*******************************************************************************
// * SORMAS® - Surveillance Outbreak Response Management & Analysis System
// * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <https://www.gnu.org/licenses/>.
// *******************************************************************************/
//package de.symeda.sormas.ui.statistics.caze;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import com.vaadin.server.Page;
//import com.vaadin.ui.CheckBox;
//import com.vaadin.ui.HorizontalLayout;
//import com.vaadin.ui.Label;
//import com.vaadin.ui.Notification;
//import com.vaadin.ui.RadioButtonGroup;
//import com.vaadin.ui.TextField;
//import com.vaadin.ui.VerticalLayout;
//import com.vaadin.ui.Notification.Type;
//import com.vaadin.ui.themes.ValoTheme;
//
//import de.symeda.sormas.api.AgeGroup;
//import de.symeda.sormas.api.Disease;
//import de.symeda.sormas.api.FacadeProvider;
//import de.symeda.sormas.api.IntegerRange;
//import de.symeda.sormas.api.Month;
//import de.symeda.sormas.api.MonthOfYear;
//import de.symeda.sormas.api.Quarter;
//import de.symeda.sormas.api.QuarterOfYear;
//import de.symeda.sormas.api.Year;
//import de.symeda.sormas.api.caze.CaseClassification;
//import de.symeda.sormas.api.caze.CaseOutcome;
//import de.symeda.sormas.api.i18n.Captions;
//import de.symeda.sormas.api.i18n.I18nProperties;
//import de.symeda.sormas.api.i18n.Strings;
//import de.symeda.sormas.api.i18n.Validations;
//import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
//import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
//import de.symeda.sormas.api.person.Sex;
//
//import de.symeda.sormas.api.statistics.StatisticsAttribute;
//import de.symeda.sormas.api.statistics.StatisticsAttributesContainer;
//import de.symeda.sormas.api.statistics.StatisticsAttributeEnum;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseCriteria;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseSubAttribute;
//import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttribute;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttributeEnum;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttributeGroup;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttributeGroupEnum;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttributesContainer;
//import de.symeda.sormas.api.statistics.caze.StatisticsCaseCountDto;
//import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
//import de.symeda.sormas.api.statistics.caze.StatisticsHelper;
//import de.symeda.sormas.api.statistics.contact.StatisticsContactCountDto;
//import de.symeda.sormas.api.user.DefaultUserRole;
//import de.symeda.sormas.api.statistics.StatisticsHelper.StatisticsKeyComparator;
//import de.symeda.sormas.api.utils.EpiWeek;
//import de.symeda.sormas.ui.statistics.CaseCountOrIncidence;
//import de.symeda.sormas.ui.statistics.StatisticsFilterComponent;
//import de.symeda.sormas.ui.statistics.StatisticsFilterElement;
//import de.symeda.sormas.ui.statistics.StatisticsFilterElement.TokenizableValue;
//import de.symeda.sormas.ui.utils.CssStyles;
//import de.symeda.sormas.ui.statistics.StatisticsFilterRegionDistrictElement;
//import de.symeda.sormas.ui.statistics.StatisticsView;
//import de.symeda.sormas.ui.statistics.StatisticsVisualizationType;
//
//public class CaseStatisticsView2 extends StatisticsView {
//
//	private static final long serialVersionUID = -4440568319850399685L;
//
//	public static final String VIEW_NAME = "caseStatistics";
//	
//	protected StatisticsCaseCriteria criteria;
//
//	
//	public CaseStatisticsView2() {
//		super(VIEW_NAME);
//	}
//
//	protected void populateStatisticsAttributes () {
//		List<StatisticsCaseAttributeGroup> groups = new ArrayList<StatisticsCaseAttributeGroup>();
//		
//		//time
//		List<StatisticsCaseAttribute> attributes = new ArrayList<StatisticsCaseAttribute>();
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.ONSET_TIME));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.REPORT_TIME));
//		StatisticsCaseAttributeGroup group = new StatisticsCaseAttributeGroup(StatisticsCaseAttributeGroupEnum.TIME, attributes);
//		groups.add(group);
//		
//		//place
//		attributes = new ArrayList<StatisticsCaseAttribute>();
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.REGION_DISTRICT));
//		group = new StatisticsCaseAttributeGroup(StatisticsCaseAttributeGroupEnum.PLACE, attributes);
//		groups.add(group);
//		
//		//person
//		attributes = new ArrayList<StatisticsCaseAttribute>();
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.SEX));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_1_YEAR));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_5_YEARS));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_CHILDREN_FINE));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.AGE_INTERVAL_BASIC));
//		group = new StatisticsCaseAttributeGroup(StatisticsCaseAttributeGroupEnum.PERSON, attributes);
//		groups.add(group);
//		
//		//case
//		attributes = new ArrayList<StatisticsCaseAttribute>();
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.DISEASE));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.CLASSIFICATION, CaseClassification.values()));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.OUTCOME, CaseOutcome.values()));
//		attributes.add(createAttribute(StatisticsCaseAttributeEnum.REPORTING_USER_ROLE));
//		group = new StatisticsCaseAttributeGroup(StatisticsCaseAttributeGroupEnum.CASE, attributes);
//		groups.add(group);
//		
//		statisticsAttributes = new StatisticsCaseAttributesContainer(groups);
//	}
//	
//	
//
//	StatisticsCaseAttribute createAttribute (StatisticsCaseAttributeEnum attribute, StatisticsGroupingKey[] groupingKeys) {
//		List<StatisticsCaseSubAttribute> subs = attribute.getSubAttributes() == null ? new ArrayList<>() :
//			Arrays.stream(attribute.getSubAttributes())
//				.map(n -> new StatisticsCaseSubAttribute(n, n, n.isUsedForFilters(), n.isUsedForGrouping(), new StatisticsHelper.getSubAttributeValues()))
//				.collect(Collectors.toList());
//		
//		return new StatisticsCaseAttribute(attribute, null, attribute.isSortByCaption(), attribute.isUnknownValueAllowed(), subs, groupingKeys);
//	}
//	
//	StatisticsCaseAttribute createAttribute (StatisticsCaseAttributeEnum attribute) {
//		List<StatisticsCaseSubAttribute> subs = attribute.getSubAttributes() == null ? new ArrayList<>() :
//			Arrays.stream(attribute.getSubAttributes())
//				.map(n -> new StatisticsCaseSubAttribute(n, n, n.isUsedForFilters(), n.isUsedForGrouping(), new StatisticsHelper.getSubAttributeValues()))
//				.collect(Collectors.toList());
//		
//		return new StatisticsCaseAttribute(attribute, StatisticsHelper.getEnum(attribute), attribute.isSortByCaption(), attribute.isUnknownValueAllowed(), subs, new StatisticsHelper.getAttributeValues());
//	}
//	
//	
//	protected void addOptionsLayout(VerticalLayout statisticsLayout) {
//		Label optionsTitle = new Label(I18nProperties.getCaption(Captions.options));
//		optionsTitle.setWidthUndefined();
//		CssStyles.style(optionsTitle, CssStyles.STATISTICS_TITLE);
//		statisticsLayout.addComponent(optionsTitle);
//
//		HorizontalLayout optionsLayout = new HorizontalLayout();
//		optionsLayout.setWidth(100, Unit.PERCENTAGE);
//		optionsLayout.setSpacing(true);
//		CssStyles.style(optionsLayout, CssStyles.STATISTICS_TITLE_BOX);
//		{
//			ogCaseCountOrIncidence = new RadioButtonGroup<CaseCountOrIncidence>(I18nProperties.getCaption(Captions.statisticsDataDisplayed), Arrays.asList(CaseCountOrIncidence.values()));
//			ogCaseCountOrIncidence.setValue(CaseCountOrIncidence.CASE_COUNT);
//			ogCaseCountOrIncidence.addValueChangeListener(e -> {
//				showCaseIncidence = e.getValue() == CaseCountOrIncidence.CASE_INCIDENCE;
//				tfIncidenceDivisor.setVisible(showCaseIncidence);
//				visualizationComponent.setStackedColumnAndPieEnabled(!showCaseIncidence);
//			});
//			CssStyles.style(ogCaseCountOrIncidence, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
//			optionsLayout.addComponent(ogCaseCountOrIncidence);
//
//			tfIncidenceDivisor = new TextField(I18nProperties.getCaption(Captions.statisticsIncidenceDivisor));
//			tfIncidenceDivisor.setValue("100000");
//			tfIncidenceDivisor.addValueChangeListener(e -> {
//				try {
//					// Store value in a temporary variable to trigger possible exception
//					int newDivisor = Integer.valueOf(e.getValue());
//					incidenceDivisor = newDivisor;
//				} catch (NumberFormatException ex) {
//					tfIncidenceDivisor.setValue(String.valueOf(incidenceDivisor));
//					new Notification(null, I18nProperties.getValidationError(Validations.statisticsIncidenceOnlyNumbersAllowed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
//				}
//			});
//			optionsLayout.addComponent(tfIncidenceDivisor);
//			tfIncidenceDivisor.setVisible(false);
//
//			cbShowZeroValues = new CheckBox(I18nProperties.getCaption(Captions.statisticsShowZeroValues));
//			cbShowZeroValues.setValue(false);
//			CssStyles.style(cbShowZeroValues, CssStyles.FORCE_CAPTION_CHECKBOX);
//			optionsLayout.addComponent(cbShowZeroValues);
//
//			cbHideOtherCountries = new CheckBox(I18nProperties.getCaption(Captions.dashboardHideOtherCountries));
//			cbHideOtherCountries.setValue(false);
//			CssStyles.style(cbHideOtherCountries, CssStyles.FORCE_CAPTION_CHECKBOX);
//			optionsLayout.addComponent(cbHideOtherCountries);
//			cbHideOtherCountries.setVisible(StatisticsVisualizationType.MAP.equals(visualizationComponent.getVisualizationType()));
//
//			Label expandedDummy = new Label();
//			optionsLayout.addComponent(expandedDummy);
//			optionsLayout.setExpandRatio(expandedDummy, 1);
//		}
//		statisticsLayout.addComponent(optionsLayout);
//	}
//
//	
//	protected List<StatisticsCaseCountDto> generateStatistics() {
//		fillCaseCriteria(showCaseIncidence);
//
//		if (showCaseIncidence) {	
//			hasMissingPopulationData = false;
//			caseIncidencePossible = true;
//			missingPopulationDataNames = null;
//
//			if (!visualizationComponent.hasRegionGrouping() && !visualizationComponent.hasDistrictGrouping()) {
//				// we don't have a territorial grouping, so the system will sum up the population of all regions.
//				// make sure the user is informed about regions with missing population data
//				
//				 List<Long> missingPopulationDataRegionIds = FacadeProvider.getPopulationDataFacade().getMissingPopulationDataForStatistics(criteria, false, false, visualizationComponent.hasSexGrouping(), visualizationComponent.hasAgeGroupGroupingWithPopulationData());
//				 hasMissingPopulationData = missingPopulationDataRegionIds.size() > 0;
//				 if (hasMissingPopulationData) {
//					 caseIncidencePossible = false;
//					 List<String> missingPopulationDataNamesList = FacadeProvider.getRegionFacade().getNamesByIds(missingPopulationDataRegionIds);
//					 missingPopulationDataNames = String.join(", ", missingPopulationDataNamesList);
//				 }
//			}
//
//			// Calculate projected population by either using the current year or, if a date filter has been selected, the maximum year from the date filter
//			populationReferenceYear = calculateMaximumReferenceYear(null, criteria.getOnsetYears(), Comparator.naturalOrder(), e -> e.getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportYears(), Comparator.naturalOrder(), e -> e.getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
//			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
//		}
//
//		List<StatisticsCaseCountDto> resultData = FacadeProvider.getCaseStatisticsFacade().queryCaseCount(
//			criteria,
//			StatisticsHelper.getEnum(visualizationComponent.getRowsAttribute()), 
//			StatisticsHelper.getEnum(visualizationComponent.getRowsSubAttribute()),
//			StatisticsHelper.getEnum(visualizationComponent.getColumnsAttribute()),
//			StatisticsHelper.getEnum(visualizationComponent.getColumnsSubAttribute()),
//			showCaseIncidence && caseIncidencePossible,
//			cbShowZeroValues.getValue(), 
//			populationReferenceYear
//		);
//		
//		StatisticsKeyComparator keyComparator = new StatisticsKeyComparator();
//		resultData.sort((c1, c2) -> {
//			int result = keyComparator.compare(c1.getRowKey(), c2.getRowKey());
//			if (result == 0) {
//				result = keyComparator.compare(c1.getColumnKey(), c2.getColumnKey());
//			}
//			return result;
//		});
//
//		return resultData;
//	}
//
//	protected void fillCaseCriteria(boolean showCaseIncidence) {
//		criteria = new StatisticsCaseCriteria();
//
//		for (StatisticsFilterComponent filterComponent : filterComponents) {
//			
//			StatisticsFilterElement filterElement = filterComponent.getFilterElement();
//			StatisticsCaseAttributeEnum attribute = StatisticsHelper.getEnum(filterComponent.getSelectedAttribute());
//			
//			switch (attribute) {
//			case SEX:
//				if (filterElement.getSelectedValues() != null) {
//					List<Sex> sexes = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						if (tokenizableValue.getValue().equals(I18nProperties.getString(Strings.unknown))) {
//							criteria.sexUnknown(true);
//						} else {
//							sexes.add((Sex) tokenizableValue.getValue());
//						}
//					}
//					criteria.sexes(sexes);
//				}
//				break;
//			case DISEASE:
//				if (filterElement.getSelectedValues() != null) {
//					List<Disease> diseases = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						diseases.add((Disease) tokenizableValue.getValue());
//					}
//					criteria.diseases(diseases);
//				}
//				break;
//			case CLASSIFICATION:
//				if (filterElement.getSelectedValues() != null) {
//					List<CaseClassification> classifications = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						classifications.add((CaseClassification) tokenizableValue.getValue());
//					}
//					criteria.classifications(classifications);
//				}
//				break;
//			case OUTCOME:
//				if (filterElement.getSelectedValues() != null) {
//					List<CaseOutcome> outcomes = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						outcomes.add((CaseOutcome) tokenizableValue.getValue());
//					}
//					criteria.outcomes(outcomes);
//				}
//				break;
//			case AGE_INTERVAL_1_YEAR:
//			case AGE_INTERVAL_5_YEARS:
//			case AGE_INTERVAL_CHILDREN_COARSE:
//			case AGE_INTERVAL_CHILDREN_FINE:
//			case AGE_INTERVAL_CHILDREN_MEDIUM:
//			case AGE_INTERVAL_BASIC:
//				if (filterElement.getSelectedValues() != null) {
//					List<IntegerRange> ageIntervals = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						ageIntervals.add((IntegerRange) tokenizableValue.getValue());
//					}
//					criteria.addAgeIntervals(ageIntervals);
//
//					// Fill age groups if 5 years interval has been selected and case incidence is shown
//					if (showCaseIncidence && filterComponent.getSelectedAttribute() == StatisticsCaseAttributeEnum.AGE_INTERVAL_5_YEARS) {
//						List<AgeGroup> ageGroups = new ArrayList<>();
//						for (IntegerRange ageInterval : ageIntervals) {
//							if (ageInterval.getFrom() != null || ageInterval.getTo() != null) {
//								ageGroups.add(AgeGroup.getAgeGroupFromIntegerRange(ageInterval));
//							}
//						}
//						criteria.addAgeGroups(ageGroups);
//					}
//				}
//				break;
//			case REGION_DISTRICT:
//				StatisticsFilterRegionDistrictElement regionDistrictElement = (StatisticsFilterRegionDistrictElement) filterElement;
//				if (regionDistrictElement.getSelectedRegions() != null) {
//					List<RegionReferenceDto> regions = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : regionDistrictElement.getSelectedRegions()) {
//						regions.add((RegionReferenceDto) tokenizableValue.getValue());
//					}
//					criteria.regions(regions);
//				}
//				if (regionDistrictElement.getSelectedDistricts() != null) {
//					List<DistrictReferenceDto> districts = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : regionDistrictElement.getSelectedDistricts()) {
//						districts.add((DistrictReferenceDto) tokenizableValue.getValue());
//					}
//					criteria.districts(districts);
//				}
//				break;
//			case REPORTING_USER_ROLE:
//				if (filterElement.getSelectedValues() != null) {
//					List<DefaultUserRole> reportingUserRoles = new ArrayList<>();
//					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//						reportingUserRoles.add((DefaultUserRole) tokenizableValue.getValue());
//					}
//					criteria.reportingUserRoles(reportingUserRoles);
//				}
//				break;
//			default:
//				switch (filterComponent.getSelectedSubAttribute()) {
//				case YEAR:
//					if (filterElement.getSelectedValues() != null) {
//						List<Year> years = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							years.add((Year) tokenizableValue.getValue());
//						}
//						criteria.years(years, attribute);
//					}
//					break;
//				case QUARTER:
//					if (filterElement.getSelectedValues() != null) {
//						List<Quarter> quarters = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							quarters.add((Quarter) tokenizableValue.getValue());
//						}
//						criteria.quarters(quarters, attribute);
//					}
//					break;
//				case MONTH:
//					if (filterElement.getSelectedValues() != null) {
//						List<Month> months = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							months.add((Month) tokenizableValue.getValue());
//						}
//						criteria.months(months, attribute);
//					}
//					break;
//				case EPI_WEEK:
//					if (filterElement.getSelectedValues() != null) {
//						List<EpiWeek> epiWeeks = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							epiWeeks.add((EpiWeek) tokenizableValue.getValue());
//						}
//						criteria.epiWeeks(epiWeeks, attribute);
//					}
//					break;
//				case QUARTER_OF_YEAR:
//					if (filterElement.getSelectedValues() != null) {
//						List<QuarterOfYear> quartersOfYear = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							quartersOfYear.add((QuarterOfYear) tokenizableValue.getValue());
//						}
//						criteria.quartersOfYear(quartersOfYear, attribute);
//					}
//					break;
//				case MONTH_OF_YEAR:
//					if (filterElement.getSelectedValues() != null) {
//						List<MonthOfYear> monthsOfYear = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							monthsOfYear.add((MonthOfYear) tokenizableValue.getValue());
//						}
//						criteria.monthsOfYear(monthsOfYear, attribute);
//					}
//					break;
//				case EPI_WEEK_OF_YEAR:
//					if (filterElement.getSelectedValues() != null) {
//						List<EpiWeek> epiWeeksOfYear = new ArrayList<>();
//						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
//							epiWeeksOfYear.add((EpiWeek) tokenizableValue.getValue());
//						}
//						criteria.epiWeeksOfYear(epiWeeksOfYear, attribute);
//					}
//					break;
//				case DATE_RANGE:
//					criteria.dateRange((Date) filterElement.getSelectedValues().get(0).getValue(),
//							(Date) filterElement.getSelectedValues().get(1).getValue(),
//							attribute);
//					break;
//				default:
//					throw new IllegalArgumentException(attribute.toString());
//				}
//			}
//		}
//	}
//
//	protected String getDataLabel (boolean prefersShortLabel, boolean prefersPlural) {
//		return prefersShortLabel ? 
//			I18nProperties.getString(prefersPlural ? Strings.entityCases : Strings.entityCase)
//			: (showCaseIncidence && caseIncidencePossible ? CaseCountOrIncidence.CASE_INCIDENCE : CaseCountOrIncidence.CASE_COUNT).toString();
//	}
//
//	@Override
//	protected List<StatisticsContactCountDto> generateContactStatistics() {
//		return null;
//	}
//}
