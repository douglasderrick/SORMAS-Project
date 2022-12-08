package de.symeda.sormas.ui.statistics.contact;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.Sex;

import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsAttributeEnum;
import de.symeda.sormas.api.statistics.contact.StatisticsContactCriteria;
import de.symeda.sormas.api.statistics.contact.StatisticsContactSubAttribute;
import de.symeda.sormas.api.statistics.contact.StatisticsContactSubAttributeEnum;
import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.contact.StatisticsHelper;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.statistics.StatisticsHelper.StatisticsKeyComparator;
import de.symeda.sormas.api.statistics.caze.StatisticsCaseAttributeEnum;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttribute;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttributeEnum;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttributeGroup;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttributeGroupEnum;
import de.symeda.sormas.api.statistics.contact.StatisticsContactAttributesContainer;
import de.symeda.sormas.api.statistics.contact.StatisticsContactCountDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMapUtil;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.statistics.AbstractStatisticsView;
import de.symeda.sormas.ui.statistics.ContactCountOrIncidence;
import de.symeda.sormas.ui.statistics.ContactCountOrIncidence;
import de.symeda.sormas.ui.statistics.StatisticsContactFilterComponent;
import de.symeda.sormas.ui.statistics.StatisticsContactGrid;
import de.symeda.sormas.ui.statistics.StatisticsContactVisualizationComponent;
import de.symeda.sormas.ui.statistics.StatisticsFilterComponent;
import de.symeda.sormas.ui.statistics.StatisticsFilterElement;
import de.symeda.sormas.ui.statistics.StatisticsFilterElement.TokenizableValue;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationChartType;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationComponent;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType;

public class ContactStatisticsView extends AbstractStatisticsView {

	

	

	private static final long serialVersionUID = -4440568319850399685L;

	public static final String VIEW_NAME = "contactStatistics";

	private StatisticsContactCriteria criteria;
	private Integer populationReferenceYear;

	private VerticalLayout filtersLayout;
	private VerticalLayout resultsLayout;
	private CheckBox cbShowZeroValues;
	private CheckBox cbHideOtherCountries;
	private RadioButtonGroup<ContactCountOrIncidence> ogContactCountOrIncidence;
	private TextField tfIncidenceDivisor;
	private Button exportButton;
	private final Label emptyResultLabel;
	private Label referenceYearLabel;
	private Label contactIncidenceNotPossibleLabel;
	private boolean showContactIncidence;
	private boolean hasMissingPopulationData;
	private boolean contactIncidencePossible;
	private String missingPopulationDataNames;
	private int incidenceDivisor = 100000;
	private List<StatisticsContactFilterComponent> filterComponents = new ArrayList<>();
	protected StatisticsContactVisualizationComponent visualizationComponent;
	protected boolean caseIncidencePossible;

	private Label caseIncidenceNotPossibleLabel;

	private StatisticsContactGrid statisticsContactGrid;
	
	
	public ContactStatisticsView() {
		super(VIEW_NAME);
		setWidth(100, Unit.PERCENTAGE);

		emptyResultLabel = new Label(I18nProperties.getString(Strings.infoNoContactsFoundStatistics));

		// Main layout
		VerticalLayout statisticsLayout = new VerticalLayout();
		statisticsLayout.setMargin(true);
		statisticsLayout.setSpacing(true);
		statisticsLayout.setWidth(100, Unit.PERCENTAGE);

		// Filters layout
		addFiltersLayout(statisticsLayout);

		// Visualization layout
		Label visualizationTitle = new Label(I18nProperties.getString(Strings.headingVisualization));
		visualizationTitle.setWidthUndefined();
		CssStyles.style(visualizationTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(visualizationTitle);

		visualizationComponent = new StatisticsContactVisualizationComponent();
		CssStyles.style(visualizationComponent, CssStyles.STATISTICS_TITLE_BOX);
		statisticsLayout.addComponent(visualizationComponent);
		visualizationComponent.addVisualizationTypeChangedListener(visualizationType -> {
			cbHideOtherCountries.setVisible(StatisticsVisualizationType.MAP.equals(visualizationType));
		});

		// Options layout
		addOptionsLayout(statisticsLayout);

		// Generate button
		addGenerateButton(statisticsLayout);

		// Results layout
		addResultsLayout(statisticsLayout);

		// Disclaimer
		Label disclaimer =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoStatisticsDisclaimer), ContentMode.HTML);
		statisticsLayout.addComponent(disclaimer);

		addComponent(statisticsLayout);
	}
	
	
	private void addGenerateButton(VerticalLayout statisticsLayout) {
		Button generateButton = ButtonHelper.createButton(Captions.actionGenerate, e -> {
			// Check whether there is any invalid empty filter or grouping data
			Notification errorNotification = null;
			for (StatisticsContactFilterComponent filterComponent : filterComponents) {
				// if (filterComponent.getSelectedAttributeEnum() != StatisticsAttributeEnum.REGION_DISTRICT
				// 		&& (filterComponent.getSelectedAttribute() == null
				// 		|| filterComponent.getSelectedAttribute().getSubAttributes().size() > 0
				// 		&& filterComponent.getSelectedSubAttribute() == null)) {
				if (filterComponent.getSelectedAttribute() != StatisticsContactAttributeEnum.JURISDICTION
					&& filterComponent.getSelectedAttribute() != StatisticsContactAttributeEnum.PLACE_OF_RESIDENCE
					&& (filterComponent.getSelectedAttribute() == null
						|| filterComponent.getSelectedAttribute().getSubAttributes().length > 0
							&& filterComponent.getSelectedSubAttribute() == null)) {
					errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyFilterAttributes), Type.WARNING_MESSAGE);
					break;
				}
			}

			if (showContactIncidence && hasPopulationFilterUnknownValue()) {
				errorNotification =
					new Notification(I18nProperties.getString(Strings.messageUnknownFilterAttributeForPopulationData), Type.ERROR_MESSAGE);
			}

			if (showContactIncidence
				&& (getVisualizationComponent().hasAgeGroupGroupingWithoutPopulationData() || hasUnsupportedPopulationAgeGroupFilter())) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageContactIncidenceUnsupportedAgeGroup), Type.ERROR_MESSAGE);
			}

			// if (errorNotification == null && visualizationComponent.getRowsAttribute() != null
			// 		&& visualizationComponent.getRowsAttribute().getSubAttributes().size() > 0
			// 		&& visualizationComponent.getRowsSubAttribute() == null) {
			// 	errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyRowAttribute), Type.WARNING_MESSAGE);
			// } else if (errorNotification == null && visualizationComponent.getColumnsAttribute() != null
			// 		&& visualizationComponent.getColumnsAttribute().getSubAttributes().size() > 0
			// 		&& visualizationComponent.getColumnsSubAttribute() == null) {
			if (errorNotification == null
				&& getVisualizationComponent().getRowsAttribute() != null
				&& getVisualizationComponent().getRowsAttribute().getSubAttributes().length > 0
				&& getVisualizationComponent().getRowsSubAttribute() == null) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyRowAttribute), Type.WARNING_MESSAGE);
			} else if (errorNotification == null
				&& getVisualizationComponent().getColumnsAttribute() != null
				&& getVisualizationComponent().getColumnsAttribute().getSubAttributes().length > 0
				&& getVisualizationComponent().getColumnsSubAttribute() == null) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyColumnAttribute), Type.WARNING_MESSAGE);
			}

			if (errorNotification != null) {
				errorNotification.setDelayMsec(-1);
				errorNotification.show(Page.getCurrent());
			} else {
				resultsLayout.removeAllComponents();
				switch (getVisualizationComponent().getVisualizationType()) {
				case TABLE:
					generateTable();
					break;
				case MAP:
					generateMap();
					break;
				default:
					generateChart();
					break;
				}
			}
		}, ValoTheme.BUTTON_PRIMARY);

		statisticsLayout.addComponent(generateButton);
	}
	
	protected boolean hasUnsupportedPopulationAgeGroupFilter() {
		for (StatisticsContactFilterComponent filterComponent : filterComponents) {
			// if (filterComponent.getSelectedAttributeEnum() == StatisticsAttributeEnum.AGE_INTERVAL_1_YEAR
			// 		|| filterComponent.getSelectedAttributeEnum() == StatisticsAttributeEnum.AGE_INTERVAL_BASIC
			// 		|| filterComponent.getSelectedAttributeEnum() == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE
			// 		|| filterComponent.getSelectedAttributeEnum() == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM
			// 		|| filterComponent.getSelectedAttributeEnum() == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_FINE) {
			if (filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_1_YEAR
				|| filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_BASIC
				|| filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE
				|| filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM
				|| filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_FINE) {
				return true;
			}
		}

		return false;
	}
	
	private void addResultsLayout(VerticalLayout statisticsLayout) {
		Label resultsLayoutTitle = new Label(I18nProperties.getString(Strings.headingResults));
		resultsLayoutTitle.setWidthUndefined();
		CssStyles.style(resultsLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(resultsLayoutTitle);

		resultsLayout = new VerticalLayout();
		resultsLayout.setWidth(100, Unit.PERCENTAGE);
		resultsLayout.setSpacing(true);
		CssStyles.style(resultsLayout, CssStyles.STATISTICS_TITLE_BOX);
		resultsLayout.addComponent(new Label(I18nProperties.getString(Strings.infoStatisticsResults)));

		statisticsLayout.addComponent(resultsLayout);
	}
//	protected void populateStatisticsAttributes () {
//		List<StatisticsContactAttributeGroup> groups = new ArrayList<StatisticsContactAttributeGroup>();
//		
//		//time
//		List<StatisticsContactAttribute> attributes = new ArrayList<StatisticsContactAttribute>();
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.REPORT_TIME));
//		StatisticsContactAttributeGroup group = new StatisticsContactAttributeGroup(StatisticsContactAttributeGroupEnum.TIME, attributes);
//		groups.add(group);
//		
//		//place
//		attributes = new ArrayList<StatisticsContactAttribute>();
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.REGION_DISTRICT));
//		group = new StatisticsContactAttributeGroup(StatisticsContactAttributeGroupEnum.PLACE, attributes);
//		groups.add(group);
//		
//		//person
//		attributes = new ArrayList<StatisticsContactAttribute>();
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.SEX));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_1_YEAR));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_5_YEARS));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_FINE));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.AGE_INTERVAL_BASIC));
//		group = new StatisticsContactAttributeGroup(StatisticsContactAttributeGroupEnum.PERSON, attributes);
//		groups.add(group);
//		
//		//case
//		attributes = new ArrayList<StatisticsContactAttribute>();
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.DISEASE));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.CLASSIFICATION, ContactClassification.values()));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.FOLLOW_UP_STATUS, FollowUpStatus.values()));
//		attributes.add(createAttribute(StatisticsContactAttributeEnum.REPORTING_USER_ROLE));
//		group = new StatisticsContactAttributeGroup(StatisticsContactAttributeGroupEnum.CONTACT, attributes);
//		groups.add(group);
//		
//	}
	
	private void addFiltersLayout(VerticalLayout statisticsLayout) {
		Label filtersLayoutTitle = new Label(I18nProperties.getString(Strings.headingFilters));
		filtersLayoutTitle.setWidthUndefined();
		CssStyles.style(filtersLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(filtersLayoutTitle);

		VerticalLayout filtersSectionLayout = new VerticalLayout();
		CssStyles.style(filtersSectionLayout, CssStyles.STATISTICS_TITLE_BOX);
		filtersSectionLayout.setSpacing(true);
		filtersSectionLayout.setWidth(100, Unit.PERCENTAGE);
		Label filtersInfoText = new Label(I18nProperties.getString(Strings.infoStatisticsFilter), ContentMode.HTML);
		filtersSectionLayout.addComponent(filtersInfoText);

		filtersLayout = new VerticalLayout();
		filtersLayout.setSpacing(true);
		filtersLayout.setMargin(false);
		filtersSectionLayout.addComponent(filtersLayout);

		// Filters footer
		HorizontalLayout filtersSectionFooter = new HorizontalLayout();
		{
			filtersSectionFooter.setSpacing(true);

			Button addFilterButton = ButtonHelper.createIconButton(Captions.statisticsAddFilter, VaadinIcons.PLUS, e -> {
				filtersLayout.addComponent(createFilterComponentLayout());
			}, ValoTheme.BUTTON_PRIMARY);

			filtersSectionFooter.addComponent(addFilterButton);

			Button resetFiltersButton = ButtonHelper.createButton(Captions.statisticsResetFilters, e -> {
				filtersLayout.removeAllComponents();
				filterComponents.clear();
			});

			filtersSectionFooter.addComponent(resetFiltersButton);
		}

		filtersSectionLayout.addComponent(filtersSectionFooter);
		statisticsLayout.addComponent(filtersSectionLayout);
	}

	public void generateTable() {
		List<StatisticsContactCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}

		if (showContactIncidence
			&& caseIncidencePossible
			&& populationReferenceYear != null
			&& populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(
				VaadinIcons.INFO_CIRCLE.getHtml() + " "
					+ String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear),
				ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}

		if (showContactIncidence && (!caseIncidencePossible || hasMissingPopulationData)) {
			if (!caseIncidencePossible) {
				if (hasMissingPopulationData) {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoContactIncidenceNotPossible), missingPopulationDataNames),
						ContentMode.HTML);
				} else {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoContactIncidenceIncompatible),
						ContentMode.HTML);
				}
			} else {
				caseIncidenceNotPossibleLabel = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getString(Strings.infoContactIncidenceMissingPopulationData), missingPopulationDataNames),
					ContentMode.HTML);
			}
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
			caseIncidenceNotPossibleLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(caseIncidenceNotPossibleLabel, CssStyles.VSPACE_TOP_4);
		}

		exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));

		resultsLayout.addComponent(exportButton);
		resultsLayout.setComponentAlignment(exportButton, Alignment.TOP_RIGHT);

//		 statisticsContactGrid = new statisticsContactGrid(
//		 		visualizationComponent.getRowsAttribute(), visualizationComponent.getRowsSubAttribute(),
//		 		visualizationComponent.getColumnsAttribute(), visualizationComponent.getColumnsSubAttribute(), 
//		 		showContactIncidence && caseIncidencePossible, incidenceDivisor, resultData, getDataLabel(false, true));
		statisticsContactGrid = new StatisticsContactGrid(
			getVisualizationComponent().getRowsAttribute(),
			getVisualizationComponent().getRowsSubAttribute(),
			getVisualizationComponent().getColumnsAttribute(),
			getVisualizationComponent().getColumnsSubAttribute(),
			showContactIncidence && caseIncidencePossible,
			incidenceDivisor,
			resultData,
			criteria);
		resultsLayout.addComponent(statisticsContactGrid);
		resultsLayout.setExpandRatio(statisticsContactGrid, 1);

		if (showContactIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
		}

		StreamResource streamResource = DownloadUtil.createGridExportStreamResource(
			statisticsContactGrid.getContainerDataSource(),
			statisticsContactGrid.getColumns(),
			ExportEntityName.STATISTICS);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);
	}

	public void generateChart() {
		List<StatisticsContactCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}

		if (showContactIncidence
			&& caseIncidencePossible
			&& populationReferenceYear != null
			&& populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(
				VaadinIcons.INFO_CIRCLE.getHtml() + " "
					+ String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear),
				ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}

		if (showContactIncidence && (!caseIncidencePossible || hasMissingPopulationData)) {
			if (!caseIncidencePossible) {
				if (hasMissingPopulationData) {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoContactIncidenceNotPossible), missingPopulationDataNames),
						ContentMode.HTML);
				} else {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoContactIncidenceIncompatible),
						ContentMode.HTML);
				}
			} else {
				caseIncidenceNotPossibleLabel = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getString(Strings.infoContactIncidenceMissingPopulationData), missingPopulationDataNames),
					ContentMode.HTML);
			}
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
			caseIncidenceNotPossibleLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(caseIncidenceNotPossibleLabel, CssStyles.VSPACE_TOP_4);
		}

		StatisticsVisualizationChartType chartType = getVisualizationComponent().getVisualizationChartType();
		StatisticsContactAttributeEnum xAxisAttribute = getVisualizationComponent().getColumnsAttribute();
		StatisticsContactSubAttributeEnum xAxisSubAttribute = getVisualizationComponent().getColumnsSubAttribute();
		StatisticsContactAttributeEnum seriesAttribute = getVisualizationComponent().getRowsAttribute();
		StatisticsContactSubAttributeEnum seriesSubAttribute = getVisualizationComponent().getRowsSubAttribute();

		HighChart chart = new HighChart();
		chart.setWidth(100, Unit.PERCENTAGE);
		chart.setHeight(580, Unit.PIXELS);

		StringBuilder hcjs = new StringBuilder();
		hcjs.append("var options = {").append("chart:{ " + " ignoreHiddenSeries: false, " + " type: '");
		switch (chartType) {
		case COLUMN:
		case STACKED_COLUMN:
			hcjs.append("column");
			break;
		case LINE:
			hcjs.append("line");
			break;
		case PIE:
			hcjs.append("pie");
			break;
		default:
			throw new IllegalArgumentException(chartType.toString());
		}

		// hcjs.append("', " + " backgroundColor: 'transparent' " + "}," + "credits:{ enabled: false }," + "exporting:{ "
		// 		+ " enabled: true," + " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }" + "},"
		// 		+ "title:{ text: '' },");
		
		// String defaultAxisLabel = getDataLabel(false, true);
		hcjs.append(
			"', " + " backgroundColor: 'transparent' " + "}," + "credits:{ enabled: false }," + "exporting:{ " + " enabled: true,"
				+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }" + "}," + "title:{ text: '' },");

		ContactCountOrIncidence dataStyle =
			showContactIncidence && caseIncidencePossible ? ContactCountOrIncidence.CONTACT_INCIDENCE : ContactCountOrIncidence.CONTACT_COUNT;

		TreeMap<StatisticsGroupingKey, String> xAxisCaptions = new TreeMap<>(new StatisticsKeyComparator());
		TreeMap<StatisticsGroupingKey, String> seriesCaptions = new TreeMap<>(new StatisticsKeyComparator());
		boolean appendUnknownXAxisCaption = false;
		if (seriesAttribute != null || xAxisAttribute != null) {
			// Build captions for x-axis and/or series
			//for (StatisticsCountDto row : resultData) {
				
			for (StatisticsContactCountDto row : resultData) {

				if (xAxisAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getColumnKey())) {
						xAxisCaptions.putIfAbsent((StatisticsGroupingKey) row.getColumnKey(), row.getColumnKey().toString());
					} else {
						appendUnknownXAxisCaption = true;
					}
				}
				if (seriesAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getRowKey())) {
						seriesCaptions.putIfAbsent((StatisticsGroupingKey) row.getRowKey(), row.getRowKey().toString());
					}
				}
			}
		}

		if (chartType != StatisticsVisualizationChartType.PIE) {

			hcjs.append("xAxis: { categories: [");
			if (xAxisAttribute != null) {
				xAxisCaptions.forEach((key, value) -> {
					hcjs.append("'").append(StringEscapeUtils.escapeEcmaScript(xAxisCaptions.get(key))).append("',");
				});

				if (appendUnknownXAxisCaption) {
					hcjs.append("'").append(getEscapedFragment(StatisticsHelper.NOT_SPECIFIED)).append("'");
				}
			} else if (seriesAttribute != null) {
				hcjs.append("'").append(seriesSubAttribute != null ? seriesSubAttribute.toString() : seriesAttribute.toString()).append("'");
			} else {
				hcjs.append("'").append(getEscapedFragment(StatisticsHelper.TOTAL)).append("'");
			}
			int numberOfCategories = xAxisAttribute != null ? appendUnknownXAxisCaption ? xAxisCaptions.size() + 1 : xAxisCaptions.size() : 1;
			hcjs.append("], min: 0, max: " + (numberOfCategories - 1) + "},");

			// hcjs.append("yAxis: { min: 0, title: { text: '").append(defaultAxisLabel)
			// .append("' },").append("allowDecimals: false, softMax: ").append(showContactIncidence && caseIncidencePossible ? 1 : 10).append(", stackLabels: { enabled: true, ")
			// .append("style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },");
			hcjs.append("yAxis: { min: 0, title: { text: '")
				.append(dataStyle)
				.append("' },")
				.append("allowDecimals: false, softMax: ")
				.append(showContactIncidence && caseIncidencePossible ? 1 : 10)
				.append(", stackLabels: { enabled: true, ")
				.append(
					"style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },");

			hcjs.append("tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}");
			if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN) {
				hcjs.append("<br/>").append(I18nProperties.getCaption(Captions.total) + ": {point.stackTotal}");
			}
			hcjs.append("'},");
		}

		hcjs.append(
			"legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },");

		hcjs.append("colors: ['#FF0000','#6691C4','#ffba08','#519e8a','#ed254e','#39a0ed','#FF8C00','#344055','#D36135','#82d173'],");

		if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN || chartType == StatisticsVisualizationChartType.COLUMN) {
			hcjs.append("plotOptions: { column: { borderWidth: 0, ");
			if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN) {
				hcjs.append("stacking: 'normal', ");
			}
			//@formatter:off
			hcjs.append("groupPadding: 0.05, pointPadding: 0, " + "dataLabels: {" + "enabled: true,"
					+ "formatter: function() { if (this.y > 0) return this.y; }," + "color: '#444',"
					+ "backgroundColor: 'rgba(255, 255, 255, 0.75)'," + "borderRadius: 3," + "padding: 3,"
					+ "style:{textOutline:'none'}" + "} } },");
			//@formatter:on
		}

		hcjs.append("series: [");
		if (seriesAttribute == null && xAxisAttribute == null) {
			// hcjs.append("{ name: '").append(defaultAxisLabel)
			// .append("', dataLabels: { allowOverlap: false }").append(", data: [['")
			// .append(defaultAxisLabel).append("',");
			hcjs.append("{ name: '")
				.append(dataStyle)
				.append("', dataLabels: { allowOverlap: false }")
				.append(", data: [['")
				.append(dataStyle)
				.append("',");
			if (!showContactIncidence || !caseIncidencePossible) {
				hcjs.append(resultData.get(0).getContactCount().toString());
			} else {
				hcjs.append(resultData.get(0).getIncidence(incidenceDivisor));
			}
			hcjs.append("]]}");
		} else if (getVisualizationComponent().getVisualizationChartType() == StatisticsVisualizationChartType.PIE) {
			// hcjs.append("{ name: '").append(defaultAxisLabel)
			// .append("', dataLabels: { allowOverlap: false }").append(", data: [");
			// TreeMap<StatisticsGroupingKey, StatisticsCountDto> seriesElements = new TreeMap<>(new StatisticsKeyComparator());
			// StatisticsCountDto unknownSeriesElement = null;
			// for (StatisticsCountDto row : resultData) {
			hcjs.append("{ name: '").append(dataStyle).append("', dataLabels: { allowOverlap: false }").append(", data: [");
			TreeMap<StatisticsGroupingKey, StatisticsContactCountDto> seriesElements = new TreeMap<>(new StatisticsKeyComparator());
			StatisticsContactCountDto unknownSeriesElement = null;
			for (StatisticsContactCountDto row : resultData) {
				Object seriesId = row.getRowKey();
				if (StatisticsHelper.isNullOrUnknown(seriesId)) {
					unknownSeriesElement = row;
				} else {
					seriesElements.put((StatisticsGroupingKey) seriesId, row);
				}
			}

			seriesElements.forEach((key, value) -> {
				Object seriesValue;
				if (!showContactIncidence || !caseIncidencePossible) {
					seriesValue = value.getContactCount();
				} else {
					seriesValue = value.getIncidence(incidenceDivisor);
				}
				Object seriesId = value.getRowKey();
				hcjs.append("['")
					.append(StringEscapeUtils.escapeEcmaScript(seriesCaptions.get(seriesId)))
					.append("',")
					.append(seriesValue)
					.append("],");
			});
			if (unknownSeriesElement != null) {
				Object seriesValue;
				if (!showContactIncidence || !caseIncidencePossible) {
					seriesValue = unknownSeriesElement.getContactCount();
				} else {
					seriesValue = unknownSeriesElement.getIncidence(incidenceDivisor);
				}
				// hcjs.append("['").append(defaultAxisLabel).append("',")
				// .append(seriesValue).append("],");
				hcjs.append("['").append(dataStyle).append("',").append(seriesValue).append("],");
			}
			hcjs.append("]}");
		} else {
			Object seriesKey = null;
			TreeMap<StatisticsGroupingKey, String> seriesStrings = new TreeMap<>(new StatisticsKeyComparator());
			final StringBuilder currentSeriesString = new StringBuilder();
			final StringBuilder unknownSeriesString = new StringBuilder();
			final StringBuilder totalSeriesString = new StringBuilder();
			TreeMap<Integer, Number> currentSeriesValues = new TreeMap<>();

			for (StatisticsContactCountDto row : resultData) {
				// Retrieve series caption of the current row
				Object rowSeriesKey;
				if (seriesAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getRowKey())) {
						rowSeriesKey = row.getRowKey();
					} else {
						rowSeriesKey = StatisticsHelper.VALUE_UNKNOWN;
					}
				} else {
					rowSeriesKey = StatisticsHelper.TOTAL;
				}

				// If the first row or a row with a new caption is processed, save the data and
				// begin a new series
				if (!DataHelper.equal(seriesKey, rowSeriesKey)) {
					finalizeChartSegment(seriesKey, currentSeriesValues, unknownSeriesString, currentSeriesString, totalSeriesString, seriesStrings);

					// Append the start sequence of the next series String
					if (StatisticsHelper.isNullOrUnknown(rowSeriesKey)) {
						seriesKey = StatisticsHelper.VALUE_UNKNOWN;
						unknownSeriesString.append("{ name: '")
							.append(getEscapedFragment(StatisticsHelper.NOT_SPECIFIED))
							.append("', dataLabels: { allowOverlap: false }, data: [");
					} else if (rowSeriesKey.equals(StatisticsHelper.TOTAL)) {
						seriesKey = StatisticsHelper.TOTAL;
						totalSeriesString.append("{name : '")
							.append(getEscapedFragment(StatisticsHelper.TOTAL))
							.append("', dataLabels: { allowOverlap: false }, data: [");
					} else {
						seriesKey = (StatisticsGroupingKey) row.getRowKey();
						currentSeriesString.append("{ name: '")
							.append(StringEscapeUtils.escapeEcmaScript(seriesCaptions.get(seriesKey)))
							.append("', dataLabels: { allowOverlap: false }, data: [");
					}
				}

				Object value;
				if (!showContactIncidence || !caseIncidencePossible) {
					value = row.getContactCount();
				} else {
					value = row.getIncidence(incidenceDivisor);
				}
				if (xAxisAttribute != null) {
					Object xAxisId = row.getColumnKey();
					int captionPosition = StatisticsHelper.isNullOrUnknown(xAxisId)
						? xAxisCaptions.size()
						: xAxisCaptions.headMap((StatisticsGroupingKey) xAxisId).size();
					currentSeriesValues.put(captionPosition, (Number) value);
				} else {
					currentSeriesValues.put(0, (Number) value);
				}
			}

			// Add the last series
			finalizeChartSegment(seriesKey, currentSeriesValues, unknownSeriesString, currentSeriesString, totalSeriesString, seriesStrings);

			seriesStrings.forEach((key, value) -> {
				hcjs.append(value);
			});

			// Add the "Unknown" series
			if (unknownSeriesString.length() > 0) {
				hcjs.append(unknownSeriesString.toString());
			}

			// Add the "Total" series
			if (totalSeriesString.length() > 0) {
				hcjs.append(totalSeriesString.toString());
			}

			// Remove last three characters to avoid invalid chart
			hcjs.delete(hcjs.length() - 3, hcjs.length());

			hcjs.append("]}");
		}
		hcjs.append("],");

		//@formatter:off
		hcjs.append("exporting: {\n" +
						"        buttons: {\n" +
						"            contextButton: {\n" +
						"                menuItems: [\n" +
						"                    'printChart',\n" +
						"                    'separator',\n" +
						"                    'downloadPNG',\n" +
						"                    'downloadJPEG',\n" +
						"                    'downloadPDF',\n" +
						"                    'downloadSVG',\n" +
						"                    'downloadCSV',\n" +
						"                    'downloadXLS'\n" +
						"                ]\n" +
						"            }\n" +
						"        }\n" +
						"    }");
		hcjs.append("};");
		//@formatter:on

		chart.setHcjs(hcjs.toString());
		resultsLayout.addComponent(chart);
		resultsLayout.setExpandRatio(chart, 1);

		if (showContactIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
		}
	}

	private String getEscapedFragment(String i18nFragmentKeykey) {
		return StringEscapeUtils.escapeEcmaScript(I18nProperties.getCaption(i18nFragmentKeykey));
	}

	private void finalizeChartSegment(
		Object seriesKey,
		TreeMap<Integer, Number> currentKeyValues,
		StringBuilder unknownKeyString,
		StringBuilder currentKeyString,
		StringBuilder totalKeyString,
		TreeMap<StatisticsGroupingKey, String> columnStrings) {
		if (seriesKey != null) {
			if (StatisticsHelper.isNullOrUnknown(seriesKey)) {
				currentKeyValues.forEach((key, value) -> {
					unknownKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				unknownKeyString.append("]},");
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			} else if (seriesKey.equals(StatisticsHelper.TOTAL)) {
				currentKeyValues.forEach((key, value) -> {
					totalKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				totalKeyString.append("]},");
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			} else {
				StatisticsGroupingKey seriesGroupingKey = (StatisticsGroupingKey) seriesKey;
				currentKeyValues.forEach((key, value) -> {
					currentKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				currentKeyString.append("]},");
				columnStrings.put(seriesGroupingKey, currentKeyString.toString());
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			}
		}
	}

	public void generateMap() {
		List<StatisticsContactCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}

		if (showContactIncidence
			&& caseIncidencePossible
			&& populationReferenceYear != null
			&& populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(
				VaadinIcons.INFO_CIRCLE.getHtml() + " "
					+ String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear),
				ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}

		if (showContactIncidence && (!caseIncidencePossible || hasMissingPopulationData)) {
			if (!caseIncidencePossible) {
				if (hasMissingPopulationData) {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoContactIncidenceNotPossible), missingPopulationDataNames),
						ContentMode.HTML);
				} else {
					caseIncidenceNotPossibleLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoContactIncidenceIncompatible),
						ContentMode.HTML);
				}
			} else {
				caseIncidenceNotPossibleLabel = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getString(Strings.infoContactIncidenceMissingPopulationData), missingPopulationDataNames),
					ContentMode.HTML);
			}
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
			caseIncidenceNotPossibleLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(caseIncidenceNotPossibleLabel, CssStyles.VSPACE_TOP_4);
		}

		HorizontalLayout mapLayout = new HorizontalLayout();
		mapLayout.setSpacing(true);
		mapLayout.setMargin(false);
		mapLayout.setWidth(100, Unit.PERCENTAGE);
		mapLayout.setHeightUndefined();

		LeafletMap map = new LeafletMap();
		map.setTileLayerOpacity(0.5f);
		map.setWidth(100, Unit.PERCENTAGE);
		map.setHeight(580, Unit.PIXELS);
		map.setZoom(FacadeProvider.getConfigFacade().getMapZoom());
		GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
		if (mapCenter != null) {
			map.setCenter(mapCenter);
		} else {
			GeoLatLon countryCenter = FacadeProvider.getConfigFacade().getCountryCenter();
			map.setCenter(countryCenter);
		}

		if (cbHideOtherCountries.getValue()) {
			LeafletMapUtil.addOtherCountriesOverlay(map);
		}

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();

		List<LeafletPolygon> outlinePolygones = new ArrayList<>();

		// draw outlines of all regions
		for (RegionReferenceDto region : regions) {

			GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(region);
			if (regionShape == null) {
				continue;
			}

			// fillOpacity is used, so we can still hover the region
			Arrays.stream(regionShape).forEach(regionShapePart -> {
				LeafletPolygon polygon = new LeafletPolygon();
				polygon.setCaption(region.getCaption());
				polygon.setOptions("{\"weight\": 1, \"color\": '#888', \"fillOpacity\": 0.02}");
				polygon.setLatLons(regionShapePart);
				outlinePolygones.add(polygon);
			});
		}

		map.addPolygonGroup("outlines", outlinePolygones);

		if (!showContactIncidence || !caseIncidencePossible) {
			// resultData.sort((a, b) -> {
			// 	return Integer.compare(a.getCount(), b.getCount());
			// });
			resultData.sort(Comparator.comparingInt(StatisticsContactCountDto::getContactCount));
		} else {
			resultData.sort((a, b) -> {
				BigDecimal incidenceA = a.getIncidence(incidenceDivisor);
				BigDecimal incidenceB = b.getIncidence(incidenceDivisor);
				return DataHelper.compare(incidenceA, incidenceB);
			});
		}

		BigDecimal valuesLowerQuartile, valuesMedian, valuesUpperQuartile;
		if (!showContactIncidence || !caseIncidencePossible) {
			// valuesLowerQuartile = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.25)).getCount()) : BigDecimal.ZERO;
			// valuesMedian = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.5)).getCount()) : BigDecimal.ZERO;
			// valuesUpperQuartile = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.75)).getCount()) : BigDecimal.ZERO;
			valuesLowerQuartile =
				resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.25)).getContactCount()) : BigDecimal.ZERO;
			valuesMedian = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.5)).getContactCount()) : BigDecimal.ZERO;
			valuesUpperQuartile =
				resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.75)).getContactCount()) : BigDecimal.ZERO;
		} else {
			valuesLowerQuartile =
				resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.25)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesLowerQuartile == null) {
				valuesLowerQuartile = BigDecimal.ZERO;
			}
			valuesMedian = resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.5)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesMedian == null) {
				valuesMedian = BigDecimal.ZERO;
			}
			valuesUpperQuartile =
				resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.75)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesUpperQuartile == null) {
				valuesUpperQuartile = BigDecimal.ZERO;
			}
		}

		List<LeafletPolygon> resultPolygons = new ArrayList<LeafletPolygon>();

		boolean hasNullValue = false;
		// Draw relevant district fills
		for (StatisticsContactCountDto resultRow : resultData) {
			ReferenceDto regionOrDistrict = (ReferenceDto) resultRow.getRowKey();
			String shapeUuid = regionOrDistrict.getUuid();
			BigDecimal regionOrDistrictValue;
			if (!showContactIncidence || !caseIncidencePossible) {
				regionOrDistrictValue = new BigDecimal(resultRow.getContactCount());
			} else {
				regionOrDistrictValue = resultRow.getIncidence(incidenceDivisor);
			}
			hasNullValue |= regionOrDistrictValue == null;

			GeoLatLon[][] shape;
			switch (getVisualizationComponent().getVisualizationMapType()) {
			case REGIONS:
				shape = FacadeProvider.getGeoShapeProvider().getRegionShape(new RegionReferenceDto(shapeUuid, null, null));
				break;
			case DISTRICTS:
				shape = FacadeProvider.getGeoShapeProvider().getDistrictShape(new DistrictReferenceDto(shapeUuid, null, null));
				break;
			default:
				throw new IllegalArgumentException(getVisualizationComponent().getVisualizationMapType().toString());
			}

			if (shape == null) {
				continue;
			}

			for (int part = 0; part < shape.length; part++) {
				GeoLatLon[] shapePart = shape[part];
				String fillColor;
				String fillOpacity = "0.8";
				if (regionOrDistrictValue == null) {
					fillColor = "#888";
				} else if (regionOrDistrictValue.compareTo(BigDecimal.ZERO) == 0) {
					fillColor = "#000";
					fillOpacity = "0";
				} else if (regionOrDistrictValue.compareTo(valuesLowerQuartile) < 0) {
					fillColor = "#FEDD6C";
				} else if (regionOrDistrictValue.compareTo(valuesMedian) < 0) {
					fillColor = "#FDBF44";
				} else if (regionOrDistrictValue.compareTo(valuesUpperQuartile) < 0) {
					fillColor = "#F47B20";
				} else {
					fillColor = "#ED1B24";
				}

				LeafletPolygon polygon = new LeafletPolygon();
				if (regionOrDistrictValue == null) {
					polygon.setCaption(regionOrDistrict.getCaption() + "<br>" + I18nProperties.getCaption(Captions.notAvailableShort));
				} else {
					polygon.setCaption(regionOrDistrict.getCaption() + "<br>" + regionOrDistrictValue);
				}
				// fillOpacity is used, so we can still hover the region
				polygon.setOptions(
					"{\"stroke\": true, \"color\": '#000000', \"weight\": 1, \"fillColor\": '" + fillColor + "', \"fillOpacity\": " + fillOpacity
						+ "}");
				polygon.setLatLons(shapePart);
				resultPolygons.add(polygon);
			}
		}
		// sort polygon array, so that polygons which are completely contained by another appear on top
		List<Integer[]> indexesToSwap = new ArrayList<>();
		for (int poly1index = 0; poly1index < resultPolygons.size(); poly1index++) {
			LeafletPolygon poly1 = resultPolygons.get(poly1index);
			for (int poly2index = poly1index; poly2index < resultPolygons.size(); poly2index++) {
				LeafletPolygon poly2 = resultPolygons.get(poly2index);
				if (poly1index == poly2index) {
					continue;
				}
				// get maximum latitude and longitude of each polygon
				// if the max/min values of poly1 are completely inside those of poly2, switch both
				if (poly1.getMaxLatLon()[0] < poly2.getMaxLatLon()[0]
					&& poly1.getMinLatLon()[0] > poly2.getMinLatLon()[0]
					&& poly1.getMaxLatLon()[1] < poly2.getMaxLatLon()[1]
					&& poly1.getMinLatLon()[1] > poly2.getMinLatLon()[1]) {
					// make sure not to change the list we are currently iterating over
					indexesToSwap.add(
						new Integer[] {
							poly1index,
							poly2index });
				}
			}
		}
		for (Integer[] swaps : indexesToSwap) {
			Collections.swap(resultPolygons, swaps[0], swaps[1]);
		}

		map.addPolygonGroup("results", resultPolygons);

		mapLayout.addComponent(map);
		mapLayout.setExpandRatio(map, 1);

		if (showContactIncidence && caseIncidencePossible) {
			valuesLowerQuartile = valuesLowerQuartile.setScale(2, RoundingMode.HALF_UP);
			valuesMedian = valuesMedian.setScale(2, RoundingMode.HALF_UP);
			valuesUpperQuartile = valuesUpperQuartile.setScale(2, RoundingMode.HALF_UP);
		}

		// AbstractOrderedLayout regionLegend = DashboardMapComponent.buildRegionLegend(true, 
		// 		showContactIncidence && caseIncidencePossible ? ContactMeasure.CASE_INCIDENCE : ContactMeasure.CASE_COUNT,
		// 				hasNullValue, valuesLowerQuartile, valuesMedian, valuesUpperQuartile, incidenceDivisor,
		// 				getDataLabel(true, false), getDataLabel(true, true));
		AbstractOrderedLayout regionLegend = DashboardMapComponent.buildRegionLegend(
			true,
			showContactIncidence && caseIncidencePossible ? CaseMeasure.CASE_INCIDENCE : CaseMeasure.CASE_COUNT,
			hasNullValue,
			valuesLowerQuartile,
			valuesMedian,
			valuesUpperQuartile,
			incidenceDivisor);
		Label legendHeader = new Label(I18nProperties.getCaption(Captions.dashboardMapKey));
		CssStyles.style(legendHeader, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		regionLegend.addComponent(legendHeader, 0);

		mapLayout.addComponent(regionLegend);
		mapLayout.setExpandRatio(regionLegend, 0);

		resultsLayout.addComponent(mapLayout);
		resultsLayout.setExpandRatio(mapLayout, 1);

		if (showContactIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(caseIncidenceNotPossibleLabel);
		}
	}

//	StatisticsContactAttribute createAttribute (StatisticsContactAttribute attribute, StatisticsGroupingKey[] groupingKeys) {
//		List<StatisticsContactSubAttribute> subs = attribute.getSubAttributes() == null ? new ArrayList<>() :
//			Arrays.stream(attribute.getSubAttributes().toArray())
//				.map(n -> new StatisticsContactSubAttribute(n, n, ((StatisticsContactSubAttribute) n).isUsedForFilters(), ((StatisticsContactSubAttribute) n).isUsedForGrouping(), new StatisticsHelper.getSubAttributeValues()))
//				.collect(Collectors.toList());
//		
//		return new StatisticsContactAttribute(attribute, null, attribute.isSortByCaption(), attribute.isUnknownValueAllowed(), subs, groupingKeys);
//	}
//	
//	StatisticsContactAttribute createAttribute (StatisticsContactAttributeEnum reportTime) {
//		List<StatisticsContactSubAttribute> subs = reportTime.getSubAttributes() == null ? new ArrayList<>() :
//			Arrays.stream(reportTime.getSubAttributes())
//				.map(n -> new StatisticsContactSubAttribute(n, n, n.isUsedForFilters(), n.isUsedForGrouping(), new StatisticsHelper.getSubAttributeValues()))
//				.collect(Collectors.toList());
//		
//		return new StatisticsContactAttribute(reportTime, StatisticsHelper.getEnum(reportTime), reportTime.isSortByCaption(), reportTime.isUnknownValueAllowed(), subs, new StatisticsHelper.getAttributeValues());
//	}
	
	
	protected void addOptionsLayout(VerticalLayout statisticsLayout) {
		Label optionsTitle = new Label(I18nProperties.getCaption(Captions.options));
		optionsTitle.setWidthUndefined();
		CssStyles.style(optionsTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(optionsTitle);

		HorizontalLayout optionsLayout = new HorizontalLayout();
		optionsLayout.setWidth(100, Unit.PERCENTAGE);
		optionsLayout.setSpacing(true);
		CssStyles.style(optionsLayout, CssStyles.STATISTICS_TITLE_BOX);
		{
			cbShowZeroValues = new CheckBox(I18nProperties.getCaption(Captions.statisticsShowZeroValues));
			cbShowZeroValues.setValue(false);
			CssStyles.style(cbShowZeroValues, CssStyles.FORCE_CAPTION_CHECKBOX);
			optionsLayout.addComponent(cbShowZeroValues);

			cbHideOtherCountries = new CheckBox(I18nProperties.getCaption(Captions.dashboardHideOtherCountries));
			cbHideOtherCountries.setValue(false);
			CssStyles.style(cbHideOtherCountries, CssStyles.FORCE_CAPTION_CHECKBOX);
			optionsLayout.addComponent(cbHideOtherCountries);
			cbHideOtherCountries.setVisible(StatisticsVisualizationType.MAP.equals(getVisualizationComponent().getVisualizationType()));

			Label expandedDummy = new Label();
			optionsLayout.addComponent(expandedDummy);
			optionsLayout.setExpandRatio(expandedDummy, 1);
		}
		statisticsLayout.addComponent(optionsLayout);
	}

	
	protected List<StatisticsContactCountDto> generateStatistics() {
		fillContactCriteria(showContactIncidence);

		if (showContactIncidence) {	
			hasMissingPopulationData = false;
			caseIncidencePossible = true;
			missingPopulationDataNames = null;

			if (!getVisualizationComponent().hasRegionGrouping() && !getVisualizationComponent().hasDistrictGrouping()) {
				// we don't have a territorial grouping, so the system will sum up the population of all regions.
				// make sure the user is informed about regions with missing population data
				
				 List<Long> missingPopulationDataRegionIds = FacadeProvider.getPopulationDataFacade().getMissingPopulationDataForStatistics(criteria, false, false, getVisualizationComponent().hasSexGrouping(), getVisualizationComponent().hasAgeGroupGroupingWithPopulationData());
				 hasMissingPopulationData = missingPopulationDataRegionIds.size() > 0;
				 if (hasMissingPopulationData) {
					 caseIncidencePossible = false;
					 List<String> missingPopulationDataNamesList = FacadeProvider.getRegionFacade().getNamesByIds(missingPopulationDataRegionIds);
					 missingPopulationDataNames = String.join(", ", missingPopulationDataNamesList);
				 }
			}

			// Calculate projected population by either using the current year or, if a date filter has been selected, the maximum year from the date filter
			populationReferenceYear = calculateMaximumReferenceYear(null, criteria.getOnsetYears(), Comparator.naturalOrder(), e -> e.getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportYears(), Comparator.naturalOrder(), e -> e.getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getOnsetEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, criteria.getReportEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
		}
		
		List<StatisticsContactCountDto> resultData = FacadeProvider.getContactStatisticsFacade().queryContactCount(
				criteria,
				getVisualizationComponent().getRowsAttribute(), 
				getVisualizationComponent().getRowsSubAttribute(),
				getVisualizationComponent().getColumnsAttribute(), 
				getVisualizationComponent().getColumnsSubAttribute(),
				showContactIncidence && caseIncidencePossible, 
				cbShowZeroValues.getValue(), 
				populationReferenceYear
			);
		
		StatisticsKeyComparator keyComparator = new StatisticsKeyComparator();
		resultData.sort((c1, c2) -> {
			int result = keyComparator.compare(c1.getRowKey(), c2.getRowKey());
			if (result == 0) {
				result = keyComparator.compare(c1.getColumnKey(), c2.getColumnKey());
			}
			return result;
		});

		return resultData;
	}
	
	public StatisticsContactVisualizationComponent getVisualizationComponent() {
		return visualizationComponent;
	}

	public void setVisualizationComponent(StatisticsContactVisualizationComponent visualizationComponent) {
		this.visualizationComponent = visualizationComponent;
	}
	private <T extends StatisticsGroupingKey> Integer calculateMaximumReferenceYear(
			Integer currentMaxYear,
			List<T> list,
			Comparator<? super T> comparator,
			Function<? super T, Integer> mapFunction) {
			Integer maxYear = null;

			if (!CollectionUtils.isEmpty(list)) {
				maxYear = (Integer) list.stream().max(comparator).map(mapFunction).orElse(null);
			}

			return currentMaxYear != null ? ((maxYear != null && maxYear > currentMaxYear) ? maxYear : currentMaxYear) : maxYear;
		}



		protected void fillContactCriteria(boolean showContactIncidence) {
			criteria = new StatisticsContactCriteria();

			for (StatisticsContactFilterComponent filterComponent : filterComponents) {

				StatisticsFilterElement filterElement = filterComponent.getFilterElement();
				StatisticsContactAttributeEnum attribute = filterComponent.getSelectedAttribute();
				StatisticsContactSubAttributeEnum subAttribute = filterComponent.getSelectedSubAttribute();

				switch (attribute) {
				case SEX:
					if (filterElement.getSelectedValues() != null) {
						List<Sex> sexes = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							if (tokenizableValue.getValue().equals(I18nProperties.getString(Strings.unknown))) {
								criteria.sexUnknown(true);
							} else {
								sexes.add((Sex) tokenizableValue.getValue());
							}
						}
						criteria.sexes(sexes);
					}
					break;
				case DISEASE:
					if (filterElement.getSelectedValues() != null) {
						List<Disease> diseases = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							diseases.add((Disease) tokenizableValue.getValue());
						}
						criteria.diseases(diseases);
					}
					break;
				case CLASSIFICATION:
					if (filterElement.getSelectedValues() != null) {
						List<ContactClassification> classifications = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							classifications.add((ContactClassification) tokenizableValue.getValue());
						}
						criteria.classifications(classifications);
					}
					break;
				case FOLLOW_UP_STATUS:
					if (filterElement.getSelectedValues() != null) {
						List<FollowUpStatus> statuses = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							statuses.add((FollowUpStatus) tokenizableValue.getValue());
						}
						criteria.contactFollowUpStatuses(statuses);
					}
					break;
				case AGE_INTERVAL_1_YEAR:
				case AGE_INTERVAL_5_YEARS:
				case AGE_INTERVAL_CHILDREN_COARSE:
				case AGE_INTERVAL_CHILDREN_FINE:
				case AGE_INTERVAL_CHILDREN_MEDIUM:
				case AGE_INTERVAL_BASIC:
					if (filterElement.getSelectedValues() != null) {
						List<IntegerRange> ageIntervals = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							ageIntervals.add((IntegerRange) tokenizableValue.getValue());
						}
						criteria.addAgeIntervals(ageIntervals);

						// Fill age groups if 5 years interval has been selected and case incidence is
						// shown
						if (showContactIncidence && filterComponent
								.getSelectedContactAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_5_YEARS) {
							List<AgeGroup> ageGroups = new ArrayList<>();
							for (IntegerRange ageInterval : ageIntervals) {
								if (ageInterval.getFrom() != null || ageInterval.getTo() != null) {
									ageGroups.add(AgeGroup.getAgeGroupFromIntegerRange(ageInterval));
								}
							}
							criteria.addAgeGroups(ageGroups);
						}
					}
					break;

				case REPORTING_USER_ROLE:
					if (filterElement.getSelectedValues() != null) {
						List<DefaultUserRole> reportingUserRoles = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							reportingUserRoles.add((DefaultUserRole) tokenizableValue.getValue());
						}
						criteria.reportingUserRoles(reportingUserRoles);
					}
					break;
				default:
					if(subAttribute!=null) {
					switch (subAttribute) {
					case YEAR:
						if (filterElement.getSelectedValues() != null) {
							List<Year> years = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								years.add((Year) tokenizableValue.getValue());
							}
							criteria.years(years, attribute);
						}
						break;
					case QUARTER:
						if (filterElement.getSelectedValues() != null) {
							List<Quarter> quarters = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								quarters.add((Quarter) tokenizableValue.getValue());
							}
							criteria.quarters(quarters, attribute);
						}
						break;
					case MONTH:
						if (filterElement.getSelectedValues() != null) {
							List<Month> months = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								months.add((Month) tokenizableValue.getValue());
							}
							criteria.months(months, attribute);
						}
						break;
					case EPI_WEEK:
						if (filterElement.getSelectedValues() != null) {
							List<EpiWeek> epiWeeks = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								epiWeeks.add((EpiWeek) tokenizableValue.getValue());
							}
							criteria.epiWeeks(epiWeeks, attribute);
						}
						break;
					case QUARTER_OF_YEAR:
						if (filterElement.getSelectedValues() != null) {
							List<QuarterOfYear> quartersOfYear = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								quartersOfYear.add((QuarterOfYear) tokenizableValue.getValue());
							}
							criteria.quartersOfYear(quartersOfYear, attribute);
						}
						break;
					case MONTH_OF_YEAR:
						if (filterElement.getSelectedValues() != null) {
							List<MonthOfYear> monthsOfYear = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								monthsOfYear.add((MonthOfYear) tokenizableValue.getValue());
							}
							criteria.monthsOfYear(monthsOfYear, attribute);
						}
						break;
					case EPI_WEEK_OF_YEAR:
						if (filterElement.getSelectedValues() != null) {
							List<EpiWeek> epiWeeksOfYear = new ArrayList<>();
							for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
								epiWeeksOfYear.add((EpiWeek) tokenizableValue.getValue());
							}
							criteria.epiWeeksOfYear(epiWeeksOfYear, attribute);
						}
						break;
					case DATE_RANGE:
						criteria.dateRange((Date) filterElement.getSelectedValues().get(0).getValue(),
								(Date) filterElement.getSelectedValues().get(1).getValue(), attribute);
						break;
					default:
						throw new IllegalArgumentException(filterComponent.getSelectedSubAttribute().toString());
					}
					}
					
//					else {
//						throw new IllegalArgumentException(filterComponent.getSelectedSubAttribute().toString());
//
//					}
				}
			}
		}

	private boolean hasPopulationFilterUnknownValue() {
		for (StatisticsContactFilterComponent filterComponent : filterComponents) {
			if (filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.SEX
				|| filterComponent.getSelectedAttribute() == StatisticsContactAttributeEnum.AGE_INTERVAL_5_YEARS) {
				for (TokenizableValue selectedValue : filterComponent.getFilterElement().getSelectedValues()) {
					if (selectedValue.getValue().toString().equals(I18nProperties.getString(Strings.notSpecified))) {
						return true;
					}
				}
			}
		}

		return false;
	}
	protected String getDataLabel (boolean prefersShortLabel, boolean prefersPlural) {
		return I18nProperties.getString(prefersPlural ? Strings.entityContacts : Strings.entityContact);
	}
	
	private HorizontalLayout createFilterComponentLayout() {
		HorizontalLayout filterComponentLayout = new HorizontalLayout();
		filterComponentLayout.setSpacing(true);
		filterComponentLayout.setWidth(100, Unit.PERCENTAGE);

		StatisticsContactFilterComponent filterComponent = new StatisticsContactFilterComponent(filtersLayout.getComponentCount());

		Button removeFilterButton = ButtonHelper.createIconButtonWithCaption("close", null, VaadinIcons.CLOSE, e -> {
			filterComponents.remove(filterComponent);
			filtersLayout.removeComponent(filterComponentLayout);
		}, CssStyles.FORCE_CAPTION);
		removeFilterButton.setDescription(I18nProperties.getCaption(Captions.statisticsRemoveFilter));

		filterComponentLayout.addComponent(removeFilterButton);
		filterComponents.add(filterComponent);
		filterComponentLayout.addComponent(filterComponent);
		filterComponentLayout.setExpandRatio(filterComponent, 1);

		return filterComponentLayout;
	}

}
