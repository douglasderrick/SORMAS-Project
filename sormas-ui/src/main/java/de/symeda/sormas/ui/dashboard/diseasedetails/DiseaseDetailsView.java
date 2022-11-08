package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import static com.vaadin.navigator.ViewChangeListener.*;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;

	public DiseaseDetailsView() {
		super(VIEW_NAME, DashboardType.DISEASE);
		//filterLayout.setInfoLabelText(I18nProperties.getString(Strings.classificationForDisease));
		//filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.setSpacing(false);
		
		dashboardDataProvider = new DashboardDataProvider();

		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.DISEASE);
		}
		if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}
		
		
		dashboardSwitcher.setValue(DashboardType.DISEASE);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			navigateToDashboardView(e);
		});
		
		//Added Component
		diseaseDetailsViewLayout = new DiseaseDetailsViewLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseDetailsViewLayout);
		dashboardLayout.setExpandRatio(diseaseDetailsViewLayout, 1);
	}

	@Override
	public void refreshDiseaseData() {
		super.refreshDiseaseData();
		if (diseaseDetailsViewLayout != null)
			diseaseDetailsViewLayout.refresh();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		dashboardDataProvider.setDisease(Disease.valueOf(event.getParameters().toString()));
		refreshDiseaseData();
	}
}
