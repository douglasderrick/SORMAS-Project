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
package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.common.Page;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/clinicalvisits")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ClinicalVisitResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<ClinicalVisitDto> getAllVisits(@PathParam("since") long since) {
		return FacadeProvider.getClinicalVisitFacade().getAllActiveClinicalVisitsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<ClinicalVisitDto> getAllVisits(@PathParam("since") long since, @PathParam("size") int size, @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getClinicalVisitFacade().getAllActiveClinicalVisitsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<ClinicalVisitDto> getByUuids(List<String> uuids) {

		List<ClinicalVisitDto> result = FacadeProvider.getClinicalVisitFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postVisits(@Valid List<ClinicalVisitDto> dtos) {

		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getClinicalVisitFacade()::saveClinicalVisit);
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getClinicalVisitFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	public Page<ClinicalVisitIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ClinicalVisitCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getClinicalVisitFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}
}
