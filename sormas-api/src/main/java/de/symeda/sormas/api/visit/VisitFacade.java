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
package de.symeda.sormas.api.visit;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface VisitFacade {

	List<VisitDto> getAllActiveVisitsAfter(Date date);

	List<VisitDto> getVisitsByCase(CaseReferenceDto caseRef);

	VisitDto getVisitByUuid(String uuid);

	VisitDto saveVisit(@Valid VisitDto dto);

	void validate(VisitDto dto);

	ExternalVisitDto saveExternalVisit(@Valid ExternalVisitDto dto);

	List<String> getAllActiveUuids();

	List<VisitDto> getAllActiveVisitsAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	List<VisitDto> getByUuids(List<String> uuids);

	void deleteVisit(String visitUuid);

	List<VisitIndexDto> getIndexList(VisitCriteria visitCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(VisitCriteria visitCriteria);

	Page<VisitIndexDto> getIndexPage(VisitCriteria visitCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<VisitExportDto> getVisitsExportList(
		VisitCriteria visitCriteria,
		Collection<String> selectedRows,
		VisitExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration);

	VisitDto getLastVisitByContact(ContactReferenceDto contactRef);

	List<VisitDto> getVisitsByContact(ContactReferenceDto contactRef);

	List<VisitDto> getVisitsByContactAndPeriod(ContactReferenceDto contactRef, Date begin, Date end);

	VisitDto getLastVisitByCase(CaseReferenceDto caseRef);
}
