package de.symeda.sormas.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/labmessages")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class LabMessageResource extends EntityDtoResource {

	@GET
	@Path("/{uuid}")
	public ExternalMessageDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getExternalMessageFacade().getByUuid(uuid);
	}

	@POST
	@Path("/indexList")
	public Page<ExternalMessageIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ExternalMessageCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getExternalMessageFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

}
