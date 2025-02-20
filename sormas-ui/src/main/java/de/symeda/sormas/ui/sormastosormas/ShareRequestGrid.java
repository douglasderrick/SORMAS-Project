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

package de.symeda.sormas.ui.sormastosormas;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import com.vaadin.ui.DescriptionGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.caze.CaseFollowUpDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestIndexDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import org.apache.commons.lang3.StringUtils;

import static de.symeda.sormas.ui.utils.FollowUpUtils.getVisitResultDescription;

public class ShareRequestGrid extends FilteredGrid<ShareRequestIndexDto, ShareRequestCriteria> {

	private static final long serialVersionUID = -7556621082342162960L;

	private static final String SHOW_MESSAGE = "showRequest";
	private static final String COLUMN_ACTIONS = "actions";

	private final ShareRequestViewType viewType;

	public ShareRequestGrid(boolean isInEagerMode, ShareRequestCriteria criteria, ShareRequestViewType viewType) {
		super(ShareRequestIndexDto.class);

		this.viewType = viewType;

		initGridColumns();

		if (isInEagerMode) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}
	}

	private void initGridColumns() {
		addShowColumn((request) -> {
			ControllerProvider.getSormasToSormasController().showRequestDetails(request, viewType);
		});
		addComponentColumn(this::createActionButtons).setId(COLUMN_ACTIONS).setSortable(false).setMinimumWidth(260);

		setColumns(
			SHOW_MESSAGE,
			ShareRequestIndexDto.UUID,
			ShareRequestIndexDto.CREATION_DATE,
			ShareRequestIndexDto.DATA_TYPE,
			ShareRequestIndexDto.ORGANIZATION_NAME,
			ShareRequestIndexDto.SENDER_NAME,
			ShareRequestIndexDto.OWNERSHIP_HANDED_OVER,
			ShareRequestIndexDto.STATUS,
			COLUMN_ACTIONS,
			ShareRequestIndexDto.COMMENT);

		((Column<ShareRequestIndexDto, String>) getColumn(ExternalMessageIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ShareRequestIndexDto, Date>) getColumn(ShareRequestIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		getColumn(ShareRequestIndexDto.OWNERSHIP_HANDED_OVER).setRenderer(new BooleanRenderer());

		getColumn(ShareRequestIndexDto.COMMENT).setDescriptionGenerator((DescriptionGenerator<ShareRequestIndexDto>) item -> item.getComment());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				column.getId().equals(COLUMN_ACTIONS) || column.getId().equals(SHOW_MESSAGE)
					? ""
					: I18nProperties.findPrefixCaption(column.getId(), SormasToSormasShareRequestDto.I18N_PREFIX, ShareRequestIndexDto.I18N_PREFIX));
		}

		setSortOrder(Collections.singletonList(new GridSortOrder<>(getColumn(ShareRequestIndexDto.CREATION_DATE), SortDirection.DESCENDING)));
	}

	private Component createActionButtons(ShareRequestIndexDto indexDto) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(true);

		if (indexDto.getStatus() == ShareRequestStatus.PENDING) {
			if (viewType == ShareRequestViewType.INCOMING) {
				layout.addComponent(ButtonHelper.createButton(Captions.actionAccept, (e) -> {
					ControllerProvider.getSormasToSormasController().acceptShareRequest(indexDto, this::reload);
				}, ValoTheme.BUTTON_SMALL));
				layout.addComponent(ButtonHelper.createButton(Captions.actionReject, (e) -> {
					ControllerProvider.getSormasToSormasController().rejectShareRequest(indexDto, this::reload);
				}, ValoTheme.BUTTON_SMALL));
			} else if (UserProvider.getCurrent().hasUserRight(UserRight.SORMAS_TO_SORMAS_SHARE)) {
				layout.addComponent(ButtonHelper.createButton(Captions.sormasToSormasRevokeShare, (e) -> {
					ControllerProvider.getSormasToSormasController().revokeShareRequest(indexDto.getUuid(), this::reload);
				}, ValoTheme.BUTTON_SMALL));
			}
		}

		return layout;
	}

	protected void addShowColumn(Consumer<ShareRequestIndexDto> handler) {

		Column<ShareRequestIndexDto, String> showColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		showColumn.setId(SHOW_MESSAGE);
		showColumn.setCaption("");
		showColumn.setSortable(false);
		showColumn.setWidth(20);

		addItemClickListener(new ShowDetailsListener<>(SHOW_MESSAGE, handler::accept));
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		DataProvider<ShareRequestIndexDto, ShareRequestCriteria> dataProvider = DataProvider.fromFilteringCallbacks(query -> {
			List<SortProperty> sortProperties = query.getSortOrders()
				.stream()
				.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
				.collect(Collectors.toList());

			return loadShareRequests(query.getFilter().orElse(null), query.getOffset(), query.getLimit(), sortProperties).stream();
		},
			query -> (int) (viewType == ShareRequestViewType.INCOMING
				? FacadeProvider.getSormasToSormasShareRequestFacade().count(query.getFilter().orElse(null))
				: FacadeProvider.getShareRequestInfoFacade().count(query.getFilter().orElse(null))));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	private List<ShareRequestIndexDto> loadShareRequests(
		ShareRequestCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		return viewType == ShareRequestViewType.INCOMING
			? FacadeProvider.getSormasToSormasShareRequestFacade().getIndexList(criteria, offset, size, sortProperties)
			: FacadeProvider.getShareRequestInfoFacade().getIndexList(criteria, offset, size, sortProperties);
	}

	public void setEagerDataProvider() {
		List<ShareRequestIndexDto> indexList = loadShareRequests(getCriteria(), null, null, null);
		ListDataProvider<ShareRequestIndexDto> dataProvider = DataProvider.fromStream(indexList.stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
