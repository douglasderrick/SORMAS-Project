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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.WeeklyReportDto;

@Path("/weeklyreports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class WeeklyReportResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<WeeklyReportDto> getAllWeeklyReports(@Context SecurityContext sc, @PathParam("since") long since) {
		return FacadeProvider.getWeeklyReportFacade().getAllWeeklyReportsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<WeeklyReportDto> getAllWeeklyReports(
		@Context SecurityContext sc,
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getWeeklyReportFacade().getAllWeeklyReportsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<WeeklyReportDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {
		List<WeeklyReportDto> result = FacadeProvider.getWeeklyReportFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postWeeklyReports(@Valid List<WeeklyReportDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getWeeklyReportFacade()::saveWeeklyReport);
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		return FacadeProvider.getWeeklyReportFacade().getAllUuids();
	}
}
