package de.symeda.sormas.ui.caze;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.hene.popupbutton.PopupButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class MergeImportedCasesGrid extends TreeGrid<CaseIndexDto> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;
	public static final String COLUMN_ACTIONS = "actions";
	public static final String COLUMN_COMPLETENESS = "completenessValue";
	public static final String COLUMN_UUID = "uuidLink";

	private CaseCriteria criteria;
	private boolean ignoreRegion;
	public int dataCount=0;

	private List<String[]> hiddenUuidPairs;

	@SuppressWarnings("unchecked")
	public MergeImportedCasesGrid() {

		super(CaseIndexDto.class);
		setSizeFull();
		setWidth(1000,Unit.PIXELS);
		setSelectionMode(SelectionMode.NONE);

		Column<CaseIndexDto, String> diseaseColumn = addColumn(caze -> DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);

		addComponentColumn(indexDto -> {
			return buildButtonLayout(indexDto);
		}).setId(COLUMN_ACTIONS);

		addComponentColumn(indexDto -> {
			Label label =
				new Label(indexDto.getCompleteness() != null ? new DecimalFormat("#").format(indexDto.getCompleteness() * 100) + " %" : "-");
			if (indexDto.getCompleteness() != null) {
				if (indexDto.getCompleteness() < 0.25f) {
					CssStyles.style(label, CssStyles.LABEL_CRITICAL);
				} else if (indexDto.getCompleteness() < 0.5f) {
					CssStyles.style(label, CssStyles.LABEL_IMPORTANT);
				} else if (indexDto.getCompleteness() < 0.75f) {
					CssStyles.style(label, CssStyles.LABEL_RELEVANT);
				} else {
					CssStyles.style(label, CssStyles.LABEL_POSITIVE);
				}
			}
			return label;
		}).setId(COLUMN_COMPLETENESS);

		addComponentColumn(indexDto -> {
			Link link = new Link(
				DataHelper.getShortUuid(indexDto.getUuid()),
				new ExternalResource(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + CaseDataView.VIEW_NAME + "/" + indexDto.getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(COLUMN_UUID);

		setColumns(
			COLUMN_UUID,
			COLUMN_DISEASE,
			CaseIndexDto.CASE_CLASSIFICATION,
			CaseIndexDto.PERSON_FIRST_NAME,
			CaseIndexDto.PERSON_LAST_NAME,
			CaseIndexDto.AGE_AND_BIRTH_DATE,
			CaseIndexDto.SEX,
			CaseIndexDto.RESPONSIBLE_DISTRICT_NAME,
			CaseIndexDto.HEALTH_FACILITY_NAME,
			CaseIndexDto.REPORT_DATE,
			CaseIndexDto.CREATION_DATE,
			COLUMN_COMPLETENESS,
			COLUMN_ACTIONS);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseIndexDto, AgeAndBirthDateDto>) getColumn(CaseIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY(),
					I18nProperties.getUserLanguage()),
			new TextRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
		getColumn(COLUMN_ACTIONS).setCaption(StringUtils.capitalize(COLUMN_ACTIONS));
		getColumn(COLUMN_UUID).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.UUID));
		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.COMPLETENESS));
		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		this.setStyleGenerator((StyleGenerator<CaseIndexDto>) item -> {
			TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
			TreeData<CaseIndexDto> data = dataProvider.getTreeData();

			if (data.getRootItems().contains(item)) {
				return "v-treegrid-parent-row";
			} else {
				return "v-treegrid-child-row";
			}
		});
	}

	@SuppressWarnings("unchecked")
	private HorizontalLayout buildButtonLayout(CaseIndexDto caze) {
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		Button btnMerge = ButtonHelper.createIconButton(Captions.actionMerge, VaadinIcons.COMPRESS_SQUARE, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(Strings.confirmationMergeCaseAndDeleteOther)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed) {
						CaseIndexDto caseToMergeAndDelete = data.getParent(caze) != null ? data.getParent(caze) : data.getChildren(caze).get(0);
						FacadeProvider.getCaseFacade().mergeCase(caze.getUuid(), caseToMergeAndDelete.getUuid());
						boolean deletePerformed = deleteCaseAsDuplicate(caze, caseToMergeAndDelete);

						if (deletePerformed && FacadeProvider.getCaseFacade().isDeleted(caseToMergeAndDelete.getUuid())) {
							reload();
							new Notification(I18nProperties.getString(Strings.messageCasesMerged), Type.TRAY_NOTIFICATION).show(Page.getCurrent());
						} else {
							new Notification(I18nProperties.getString(Strings.errorCaseMerging), Type.ERROR_MESSAGE).show(Page.getCurrent());
						}
					}
				});
		});
		Button btnPick = ButtonHelper.createIconButton(Captions.actionPick, VaadinIcons.CHECK, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(Strings.confirmationPickCaseAndDeleteOther)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed) {
						CaseIndexDto caseToDelete = data.getParent(caze) != null ? data.getParent(caze) : data.getChildren(caze).get(0);
						boolean deletePerformed = deleteCaseAsDuplicate(caze, caseToDelete);

						if (deletePerformed && FacadeProvider.getCaseFacade().isDeleted(caseToDelete.getUuid())) {
							data.removeItem(data.getParent(caze) == null ? caze : data.getParent(caze));
							dataProvider.refreshAll();
							new Notification(I18nProperties.getString(Strings.messageCaseDuplicateDeleted), Type.TRAY_NOTIFICATION)
								.show(Page.getCurrent());
						} else {
							new Notification(I18nProperties.getString(Strings.errorCaseDuplicateDeletion), Type.ERROR_MESSAGE)
								.show(Page.getCurrent());
						}
					}
				});
		});

		Button btnHide = null;

		if (data.getParent(caze) == null) {
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);

			btnHide = ButtonHelper.createIconButton(Captions.actionHide, VaadinIcons.CLOSE, e -> {
				hiddenUuidPairs.add(
					new String[] {
						caze.getUuid(),
						data.getChildren(caze).get(0).getUuid() });
				dataProvider.getTreeData().removeItem(caze);
				dataProvider.refreshAll();
			});
		} else {
			CssStyles.style(btnMerge, ValoTheme.BUTTON_LINK);
			CssStyles.style(btnPick, ValoTheme.BUTTON_LINK);
		}

		if (ignoreRegion) {
			btnMerge.setEnabled(false);
			btnPick.setEnabled(false);
		}
		layout.addComponent(btnMerge);
		layout.addComponent(btnPick);

		if (btnHide != null) {
			layout.addComponent(btnHide);
		}

		return layout;
	}

	private boolean deleteCaseAsDuplicate(CaseIndexDto caze, CaseIndexDto caseToMergeAndDelete) {
		FacadeProvider.getCaseFacade().deleteCaseAsDuplicate(caseToMergeAndDelete.getUuid(), caze.getUuid());

		return true;

//		try {
//			FacadeProvider.getCaseFacade().deleteCaseAsDuplicate(caseToMergeAndDelete.getUuid(), caze.getUuid());
//		} catch (ExternalSurveillanceToolException e) {
//			return false;
//		}
	}



	@SuppressWarnings("unchecked")
	public void reload() {
		long startTime = System.currentTimeMillis();
		System.out.println("Turnaround time1 starting:_____ " + startTime + " milliseconds");


		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();
		data.clear();

		if (hiddenUuidPairs == null) {
			hiddenUuidPairs = new ArrayList<>();
		}
		//delay
		List<CaseIndexDto[]> casePairs = FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, ignoreRegion);

		long endTime = System.currentTimeMillis();
		long turnaroundTime = endTime - startTime;

		System.out.println("Turnaround time1: " + turnaroundTime + " milliseconds");

//if(casePairs>0){
		for (CaseIndexDto[] casePair : casePairs) {
			boolean uuidPairExists = false;
			for (String[] hiddenUuidPair : hiddenUuidPairs) {
				if (hiddenUuidPair[0].equals(casePair[0].getUuid()) && hiddenUuidPair[1].equals(casePair[1].getUuid())) {
					uuidPairExists = true;
				}
			}

			if (uuidPairExists) {
				continue;
			}

			data.addItem(null, casePair[0]);
			data.addItem(casePair[0], casePair[1]);
		}
		dataCount = casePairs.size();

		expandRecursively(data.getRootItems(), 0);

		//no delay
		dataProvider.refreshAll();


	}



	public void reload(boolean ignoreRegion) {
		this.ignoreRegion = ignoreRegion;
		reload();
	}

	@SuppressWarnings("unchecked")
	public void calculateCompletenessValues() {
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();

		for (CaseIndexDto parent : data.getRootItems()) {
			FacadeProvider.getCaseFacade().updateCompleteness(parent.getUuid());
			FacadeProvider.getCaseFacade().updateCompleteness(data.getChildren(parent).get(0).getUuid());
		}

		reload();
	}

	public void setCriteria(CaseCriteria criteria) {
		this.criteria = criteria;
	}

	public CaseCriteria getCriteria() {
		return criteria;
	}

	public void addExportButton(
			StreamResource streamResource,
			VerticalLayout exportLayout,
			Resource icon,
			String captionKey,
			String descriptionKey) {

		Button exportButton = ButtonHelper.createIconButton(captionKey, icon, e -> {

			Button button = e.getButton();
			int buttonPos = exportLayout.getComponentIndex(button);

			DownloadUtil.showExportWaitDialog(button, ce -> {
				//restore the button
				exportLayout.addComponent(button, buttonPos);
				button.setEnabled(true);
			});
//			exportPopupButton.setPopupVisible(false);
		}, ValoTheme.BUTTON_PRIMARY);

		exportButton.setDisableOnClick(true);
		exportButton.setDescription(I18nProperties.getDescription(descriptionKey));
		exportButton.setWidth(100, Unit.PERCENTAGE);

		exportLayout.addComponent(exportButton);

		new FileDownloader(streamResource).extend(exportButton);
	}
}
