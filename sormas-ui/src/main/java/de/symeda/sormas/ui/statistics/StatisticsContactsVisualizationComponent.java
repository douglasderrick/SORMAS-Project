package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsContactAttribute;
import de.symeda.sormas.api.statistics.StatisticsContactSubAttribute;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationChartType;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationMapType;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsContactsVisualizationComponent extends HorizontalLayout {

	private StatisticsVisualizationType visualizationType;
	private StatisticsVisualizationMapType visualizationMapType;
	private StatisticsVisualizationChartType visualizationChartType;

	private final OptionGroup visualizationSelect;
	private final OptionGroup visualizationMapSelect;
	private final OptionGroup visualizationChartSelect;
	private StatisticsContactVisualizationElement rowsElement;
	private StatisticsContactVisualizationElement columnsElement;
	private Button switchRowsAndColumnsButton;
	private final List<Consumer<StatisticsVisualizationType>> visualizationTypeChangedListeners =
		new ArrayList<Consumer<StatisticsVisualizationType>>();

	public StatisticsContactsVisualizationComponent() {
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);

		visualizationSelect =
			new OptionGroup(I18nProperties.getCaption(Captions.statisticsVisualizationType), Arrays.asList(StatisticsVisualizationType.values()));
		visualizationSelect.setId(Captions.statisticsVisualizationType);
		visualizationSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationType = (StatisticsVisualizationType) event.getProperty().getValue();
				updateComponentVisibility();
				if (rowsElement != null) {
					rowsElement.setType(rowsElement.getType(), visualizationType);
				}
				if (columnsElement != null) {
					columnsElement.setType(columnsElement.getType(), visualizationType);
				}

				for (Consumer<StatisticsVisualizationType> visualizationTypeChangedListener : visualizationTypeChangedListeners) {
					visualizationTypeChangedListener.accept(visualizationType);
				}
			}
		});
		CssStyles.style(visualizationSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationSelect.setNullSelectionAllowed(false);
		addComponent(visualizationSelect);
		setExpandRatio(visualizationSelect, 0);

		visualizationMapSelect =
			new OptionGroup(I18nProperties.getCaption(Captions.statisticsMapType), Arrays.asList(StatisticsVisualizationMapType.values()));
		visualizationMapSelect.setId(Captions.statisticsMapType);
		visualizationMapSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationMapType = (StatisticsVisualizationMapType) event.getProperty().getValue();
			}
		});
		CssStyles.style(visualizationMapSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationMapSelect.setNullSelectionAllowed(false);
		addComponent(visualizationMapSelect);
		setExpandRatio(visualizationSelect, 0);

		visualizationChartSelect =
			new OptionGroup(I18nProperties.getCaption(Captions.statisticsChartType), Arrays.asList(StatisticsVisualizationChartType.values()));
		visualizationChartSelect.setId(Captions.statisticsChartType);
		visualizationChartSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationChartType = (StatisticsVisualizationChartType) event.getProperty().getValue();
				updateComponentVisibility();
			}
		});
		CssStyles.style(visualizationChartSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationChartSelect.setNullSelectionAllowed(false);
		addComponent(visualizationChartSelect);
		setExpandRatio(visualizationChartSelect, 0);

		rowsElement = new StatisticsContactVisualizationElement(StatisticsVisualizationElementType.ROWS, visualizationType);
		addComponent(rowsElement);
		setExpandRatio(rowsElement, 0);

		switchRowsAndColumnsButton = ButtonHelper.createIconButtonWithCaption("switchRowsAndColumns", null, VaadinIcons.EXCHANGE, event -> {
			StatisticsContactVisualizationElement newRowsElement = columnsElement;
			newRowsElement.setType(StatisticsVisualizationElementType.ROWS, visualizationType);
			StatisticsContactVisualizationElement newColumnsElement = rowsElement;
			newColumnsElement.setType(StatisticsVisualizationElementType.COLUMNS, visualizationType);
			removeComponent(rowsElement);
			removeComponent(columnsElement);
			addComponent(newRowsElement, getComponentIndex(switchRowsAndColumnsButton));
			addComponent(newColumnsElement, getComponentIndex(switchRowsAndColumnsButton) + 1);
			replaceComponent(rowsElement, newRowsElement);
			replaceComponent(columnsElement, newColumnsElement);
			rowsElement = newRowsElement;
			columnsElement = newColumnsElement;
		}, CssStyles.FORCE_CAPTION);
		switchRowsAndColumnsButton.setDescription(I18nProperties.getCaption(Captions.statisticsExchange));

		addComponent(switchRowsAndColumnsButton);
		setExpandRatio(switchRowsAndColumnsButton, 0);

		columnsElement = new StatisticsContactVisualizationElement(StatisticsVisualizationElementType.COLUMNS, visualizationType);
		addComponent(columnsElement);
		setExpandRatio(columnsElement, 0);

		Label spacer = new Label();
		addComponent(spacer);
		setExpandRatio(spacer, 1);

		visualizationSelect.setValue(StatisticsVisualizationType.TABLE);
		visualizationChartSelect.setValue(StatisticsVisualizationChartType.STACKED_COLUMN);
		visualizationMapSelect.setValue(StatisticsVisualizationMapType.REGIONS);
	}
	
	private void updateComponentVisibility() {
		visualizationMapSelect.setVisible(visualizationType == StatisticsVisualizationType.MAP);
		visualizationChartSelect.setVisible(visualizationType == StatisticsVisualizationType.CHART);

		rowsElement.setVisible(visualizationType == StatisticsVisualizationType.TABLE || visualizationType == StatisticsVisualizationType.CHART);

		switchRowsAndColumnsButton.setVisible(
			visualizationType == StatisticsVisualizationType.TABLE
				|| (visualizationType == StatisticsVisualizationType.CHART && visualizationChartType != StatisticsVisualizationChartType.PIE));

		columnsElement.setVisible(
			visualizationType == StatisticsVisualizationType.TABLE
				|| (visualizationType == StatisticsVisualizationType.CHART && visualizationChartType != StatisticsVisualizationChartType.PIE));
	}

	public StatisticsContactAttribute getRowsAttribute() {
		switch (visualizationType) {
		case MAP:
			return StatisticsContactAttribute.JURISDICTION;
		default:
			break;
		}
		return rowsElement.getContactAttribute();
	}
	
	public StatisticsContactSubAttribute getRowsSubAttribute() {
		switch (visualizationType) {
		case MAP:
			switch (visualizationMapType) {
			case REGIONS:
				return StatisticsContactSubAttribute.REGION;
			case DISTRICTS:
				return StatisticsContactSubAttribute.DISTRICT;
			default:
				throw new IllegalArgumentException(visualizationMapType.toString());
			}
		default:
			break;
		}
		return rowsElement.getContactSubAttribute();
	}
	
	public StatisticsContactAttribute getColumnsAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;
		case CHART:
			switch (visualizationChartType) {
			case PIE:
				return null;
			default:
				break;
			}
		default:
			break;
		}
		return columnsElement.getContactAttribute();
	}

	public StatisticsContactSubAttribute getColumnsSubAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;
		case CHART:
			switch (visualizationChartType) {
			case PIE:
				return null;
			default:
				break;
			}
		default:
			break;
		}
		return columnsElement.getContactSubAttribute();
	}
	
	public StatisticsVisualizationType getVisualizationType() {
		return visualizationType;
	}

	public StatisticsVisualizationMapType getVisualizationMapType() {
		return visualizationMapType;
	}

	public StatisticsVisualizationChartType getVisualizationChartType() {
		return visualizationChartType;
	}
	
	public boolean hasRegionGrouping() {
		switch (visualizationType) {
		case TABLE:
		case CHART:
			return rowsElement.getContactSubAttribute() == StatisticsContactSubAttribute.REGION
				|| columnsElement.getContactSubAttribute() == StatisticsContactSubAttribute.REGION;
		case MAP:
			return visualizationMapType == StatisticsVisualizationMapType.REGIONS;
		default:
			throw new IllegalArgumentException(visualizationType.toString());
		}
	}

	public boolean hasDistrictGrouping() {
		switch (visualizationType) {
		case TABLE:
		case CHART:
			return rowsElement.getContactSubAttribute() == StatisticsContactSubAttribute.DISTRICT
				|| columnsElement.getContactSubAttribute() == StatisticsContactSubAttribute.DISTRICT;
		case MAP:
			return visualizationMapType == StatisticsVisualizationMapType.DISTRICTS;
		default:
			throw new IllegalArgumentException(visualizationType.toString());
		}
	}

	public boolean hasCommunityGrouping() {
		switch (visualizationType) {
			case TABLE:
			case CHART:
				return rowsElement.getContactSubAttribute() == StatisticsContactSubAttribute.COMMUNITY
						|| columnsElement.getContactSubAttribute() == StatisticsContactSubAttribute.COMMUNITY;
			//TODO: Community Grouping on this Visualisationtype may be implemented later
			case MAP:
				return false;
			default:
				throw new IllegalArgumentException(visualizationType.toString());
		}
	}
	
	public boolean hasIncidenceIncompatibleGrouping() {
		return rowsElement.getContactSubAttribute() == StatisticsContactSubAttribute.DISTRICT
			|| columnsElement.getContactSubAttribute() == StatisticsContactSubAttribute.DISTRICT;
	}

	public boolean hasSexGrouping() {
		switch (visualizationType) {
		case TABLE:
		case CHART:
			return rowsElement.getContactAttribute() == StatisticsContactAttribute.SEX || columnsElement.getContactAttribute() == StatisticsContactAttribute.SEX;
		case MAP:
			return false;
		default:
			throw new IllegalArgumentException(visualizationType.toString());
		}
	}

	public boolean hasAgeGroupGroupingWithPopulationData() {
		switch (visualizationType) {
		case TABLE:
		case CHART:
			return rowsElement.getContactAttribute() == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS
				|| columnsElement.getContactAttribute() == StatisticsContactAttribute.AGE_INTERVAL_5_YEARS;
		case MAP:
			return false;
		default:
			throw new IllegalArgumentException(visualizationType.toString());
		}
	}

	public boolean hasAgeGroupGroupingWithoutPopulationData() {
		switch (visualizationType) {
		case TABLE:
		case CHART:
			return (rowsElement.getContactAttribute() != null
				&& rowsElement.getContactAttribute().isAgeGroup()
				&& rowsElement.getContactAttribute() != StatisticsContactAttribute.AGE_INTERVAL_5_YEARS)
				|| (columnsElement.getContactAttribute() != null
					&& columnsElement.getContactAttribute().isAgeGroup()
					&& columnsElement.getContactAttribute() != StatisticsContactAttribute.AGE_INTERVAL_5_YEARS);
		case MAP:
			return false;
		default:
			throw new IllegalArgumentException(visualizationType.toString());
		}
	}

	public boolean hasPopulationGrouping() {
		return hasRegionGrouping() || hasDistrictGrouping() || hasSexGrouping() || hasAgeGroupGroupingWithPopulationData();
	}

	public void setStackedColumnAndPieEnabled(boolean enabled) {
		visualizationChartSelect.setItemEnabled(StatisticsVisualizationChartType.STACKED_COLUMN, enabled);
		visualizationChartSelect.setItemEnabled(StatisticsVisualizationChartType.PIE, enabled);

		if (!enabled
			&& (StatisticsVisualizationChartType.STACKED_COLUMN == visualizationChartSelect.getValue()
				|| StatisticsVisualizationChartType.PIE == visualizationChartSelect.getValue())) {
			visualizationChartSelect.setValue(StatisticsVisualizationChartType.COLUMN);
		}
	}

	public void addVisualizationTypeChangedListener(Consumer<StatisticsVisualizationType> visualizationTypeChangedListener) {
		visualizationTypeChangedListeners.add(visualizationTypeChangedListener);
	}

	public void removeVisualizationTypeChangedListener(Consumer<StatisticsVisualizationType> visualizationTypeChangedListener) {
		visualizationTypeChangedListeners.remove(visualizationTypeChangedListener);
	}

}
