package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.contacts.components.ContactsFilterLayout;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import static com.vaadin.navigator.ViewChangeListener.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;
	
	public static  String data;
	public static void setData(String newData) {
		  data=newData;
	}
	
//	public static void setProvider(DashboardDataProvider newDashboardDataProvider) {
//		dashboardDataProvider=newDashboardDataProvider;
//	}
	public DiseaseDetailsView() {
		super(VIEW_NAME, DashboardType.DISEASE);

		dashboardLayout.setSpacing(false);

		String paramData = (String) SormasUI.get().getSession().getAttribute("paramdata");

		System.out.println("paramdata");

		System.out.println(data);

		if (data != null) {

			String dateFrom = data.split("/")[0];

			String dateTo = data.split("/")[1];

			String type = data.split("/")[2];

			String caseClassification = data.split("/")[3];

			String newCaseDateType = data.split("/")[4];

			//String region = data.split("/")[5];

			if (dateFrom != null) {
				System.out.println("print dateFrom");
				System.out.println(dateFrom);

				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				String string1 = dateFrom;
				try {
					Date result1 = df1.parse(string1);
					dashboardDataProvider.setFromDate(result1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// dashboardDataProvider.setFromDate(new Date(params[1]));
			}

			if (dateTo != null) {
				System.out.println("print dateTo");
				System.out.println(dateTo);

				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				String string1 = dateTo;
				try {
					Date result1 = df1.parse(string1);
					dashboardDataProvider.setToDate(result1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// dashboardDataProvider.setFromDate(new Date(params[2]));
			}

			if (type != null) {
				System.out.println("==type==");
				System.out.println(type);

				// NewDateFilterType.values();
				dashboardDataProvider.setDateFilterType(EnumSet.allOf(NewDateFilterType.class).stream()
						.filter(e -> e.name().equals(type)).findFirst()
						.orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", type))));

				// dashboardDataProvider.setFromDate(new Date(params[2]));
			}

			// dashboardDataProvider.setRegion(region);

			if (caseClassification != null) {
				System.out.println("print caseClassification");

				String caseClass = caseClassification.replace("case", "").toUpperCase().trim();

				if (!caseClass.equals("NULL")) {

					System.out.println(caseClass);

					dashboardDataProvider.setCaseClassification(EnumSet.allOf(CaseClassification.class).stream()
							.filter(e -> e.name().equals(caseClass)).findFirst().orElseThrow(
									() -> new IllegalStateException(String.format("Unsupported type %s.", caseClass))));

					// dashboardDataProvider.setFromDate(new Date(params[2]));
					//dashboardDataProvider.setCaseClassification(CaseClassification.CONFIRMED);

				}
			} else {
				dashboardDataProvider.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
			}
			


			if (newCaseDateType != null) {
				System.out.println("==newCaseDateType==");
				System.out.println(newCaseDateType);
				NewCaseDateType enumNewCaseDateType;
				switch (newCaseDateType) {
				case "Creation date":
					enumNewCaseDateType = NewCaseDateType.CREATION;
					break;
				case "Investigation date":
					enumNewCaseDateType = NewCaseDateType.INVESTIGATION;
					break;
				case "Most relevant date":
					enumNewCaseDateType = NewCaseDateType.MOST_RELEVANT;
					break;
				case "Symptom onset date":
					enumNewCaseDateType = NewCaseDateType.ONSET;
					break;
				case "Case report date":
					enumNewCaseDateType = NewCaseDateType.REPORT;
					break;
				case "Classification date":
					enumNewCaseDateType = NewCaseDateType.CLASSIFICATION;
					break;
				default:
					enumNewCaseDateType = NewCaseDateType.MOST_RELEVANT;
				}

				dashboardDataProvider.setNewCaseDateType(enumNewCaseDateType);
			}

		}
		// dashboardDataProvider = new DashboardDataProvider();
		// dashboardDataProvider = new DashboardDataProvider(NewCaseDateType.class);

		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.DISEASE);
		}

		if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}

		// dashboardDataProvider.setNewCaseDateType(NewCaseDateType.MOST_RELEVANT);

		// filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		// filterLayout.setInfoLabelText(I18nProperties.getString(Strings.classificationForDisease));
//		
		

		filterLayout = new DiseaseFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);

		dashboardSwitcher.setValue(DashboardType.DISEASE);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			navigateToDashboardView(e);
		});

		// Added Component
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

		//System.out.println(event.getParameters().toString());
		
		dashboardDataProvider.setDisease(Disease.valueOf(event.getParameters().toString()));

//		 if(event.getParameters() != null){
//	           // split at "/", add each part as a label
//	           String[] params = event.getParameters().split("/");
//	           System.out.println("printing parameters");
//	           
//	           System.out.println(params[0]);
//	           if((String)params[0]!=null&&!params[0].equals("null")) {
//	        	   dashboardDataProvider.setDisease(Disease.valueOf((String)params[0]));
//	           }
//	       
//	       	System.out.println("enter dashboardDataProvider.getFromDate()");
//
//			System.out.println(dashboardDataProvider.getFromDate());
//			
//	           if(params[1]!=null&&!params[1].equals("null")) {
//	        	   System.out.println("print params[1]");
//	        	   System.out.println(params[1]);
//	        	   
//	        	   DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
//	        	   String string1 = params[1];
//	        	   try {
//					Date result1 = df1.parse(string1);
//					dashboardDataProvider.setFromDate(result1);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        	   //dashboardDataProvider.setFromDate(new Date(params[1]));
//	           }
//	           
//	           if(params[2]!=null&&!params[2].equals("null")) {
//	        	   System.out.println("print params[2]");
//	        	   System.out.println(params[2]);
//	        	   
//	        	   
//	        	   DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
//	        	   String string1 = params[2];
//	        	   try {
//					Date result1 = df1.parse(string1);
//					dashboardDataProvider.setToDate(result1);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        	   
//	        	   //dashboardDataProvider.setFromDate(new Date(params[2]));
//	           }
//	           
//	           if(params[3]!=null&&!params[3].equals("null")) {
//	        	   System.out.println("print params[3]");
//	        	   System.out.println(params[3]);
//	        	   
//	        	   String type = params[3];
//	        	 
//	        	   
//	        	   
//	        	  // NewDateFilterType.values();
//					dashboardDataProvider.setDateFilterType(EnumSet.allOf(NewDateFilterType.class)
//				               .stream()
//				               .filter(e -> e.name().equals(type))
//				               .findFirst()
//				               .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", type))));
//				
//	        	   
//	        	   //dashboardDataProvider.setFromDate(new Date(params[2]));
//	           }
//	   		
//	      }
//		 
		//filterLayout.reload(event);

		refreshDiseaseData();
		//filterLayout = new DiseaseFilterLayout(this, dashboardDataProvider);
//				filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
//				dashboardLayout.addComponent(filterLayout);
	}

	
	
	
	 
}
