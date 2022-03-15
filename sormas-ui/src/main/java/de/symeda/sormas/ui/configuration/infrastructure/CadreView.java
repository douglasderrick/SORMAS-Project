/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Collections;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.infrastructure.components.SearchField;
import de.symeda.sormas.ui.utils.*;

public class CadreView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/cadre";
	protected Button createButton;
	protected Button importButton;
	protected Button importDefaultContinentsButton;
	private ContinentCriteria criteria;
	private ViewConfiguration viewConfiguration;
	// Filter
	private SearchField searchField;
	private ComboBox relevanceStatusFilter;
	private Button resetButton;
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private ContinentsGrid grid;
	private MenuBar bulkOperationsDropdown;
	private RowCount rowCount;

	public CadreView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(CadreView.class).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(CadreView.class).get(ContinentCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		grid = new ContinentsGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		rowCount = new RowCount(Strings.labelNumberOfContinents, grid.getItemCount());
		gridLayout.addComponent(rowCount);
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		boolean infrastructureDataEditable = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		if (infrastructureDataEditable && UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
			importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.CONTINENT));
				window.setCaption(I18nProperties.getString(Strings.headingImportContinents));
				window.addCloseListener(c -> grid.reload(true));
			}, ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(importButton);

			importDefaultContinentsButton = ButtonHelper.createIconButton(Captions.actionImportAllContinents, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new ImportDefaultContinentsLayout());
				window.setCaption(I18nProperties.getString(Strings.headingImportAllContinents));
				window.addCloseListener(c -> grid.reload(true));
			}, ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(importDefaultContinentsButton);
		} else if (!infrastructureDataEditable) {
			Label infrastructureDataLocked = new Label();
			infrastructureDataLocked.setCaption(I18nProperties.getString(Strings.headingInfrastructureLocked));
			infrastructureDataLocked.setValue(I18nProperties.getString(Strings.messageInfrastructureLocked));
			infrastructureDataLocked.setIcon(VaadinIcons.WARNING);
			addHeaderComponent(infrastructureDataLocked);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			addHeaderComponent(exportButton);

			StreamResource streamResource = GridExportStreamResource.createStreamResource(
				grid,
				ExportEntityName.CONTINENTS,
				Collections.singletonList(ContinentsGrid.EDIT_BTN_ID),
				Collections.singletonList(ContinentIndexDto.DEFAULT_NAME));
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if (infrastructureDataEditable && UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.actionNewEntry,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getInfrastructureController().createContinent(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				viewConfiguration.setInEagerMode(true);
				bulkOperationsDropdown.setVisible(isBulkOperationsDropdownVisible());
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				searchField.setEnabled(false);
				grid.setInEagerMode(true);
				grid.reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				viewConfiguration.setInEagerMode(false);
				bulkOperationsDropdown.setVisible(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				searchField.setEnabled(true);
				navigateTo(criteria);
			});
		}

		addComponent(gridLayout);
	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		searchField = new SearchField();
		searchField.addTextChangeListener(e -> {
			criteria.nameLike(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CadreView.class).remove(ContinentCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);
		resetButton.setVisible(false);

		filterLayout.addComponent(resetButton);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(220, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.continentActiveContinents));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.continentArchivedContinents));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.continentAllContinents));
				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);

				// Bulk operation dropdown
				if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
					bulkOperationsDropdown = MenuBarHelper.createDropDown(
						Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionArchive), VaadinIcons.ARCHIVE, selectedItem -> {
							ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									true,
									grid.asMultiSelect().getSelectedItems(),
									InfrastructureType.CONTINENT,
									() -> navigateTo(criteria));
						}, EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionDearchive), VaadinIcons.ARCHIVE, selectedItem -> {
							ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									false,
									grid.asMultiSelect().getSelectedItems(),
									InfrastructureType.CONTINENT,
									() -> navigateTo(criteria));
						}, EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));

					bulkOperationsDropdown.setVisible(isBulkOperationsDropdownVisible());
					actionButtonsLayout.addComponent(bulkOperationsDropdown);
				}
			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
		rowCount.update(grid.getItemCount());
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getNameLike());

		applyingCriteria = false;
	}

	private boolean isBulkOperationsDropdownVisible() {
		boolean infrastructureDataEditable = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		return viewConfiguration.isInEagerMode()
				&& (EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())
				|| (infrastructureDataEditable && EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));
	}
}
