/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.ui.epidata;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.contact.AbstractContactView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

@SuppressWarnings("serial")
public class ContactEpiDataView extends AbstractContactView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/epidata";

	public ContactEpiDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		CommitDiscardWrapperComponent<EpiDataForm> epidDataForm =
			ControllerProvider.getContactController().getEpiDataComponent(getContactRef().getUuid(), isEditAllowed());
		setSubComponent(epidDataForm);
		setEditPermission(epidDataForm, EpiDataDto.EXPOSURES);
	}
}
