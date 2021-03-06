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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryView;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoView;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseView;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.therapy.TherapyView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	public static final String VIEW_MODE_URL_PREFIX = "v";

	public static final String ROOT_VIEW_NAME = CasesView.VIEW_NAME;

	private CaseReferenceDto caseRef = null;
	private Boolean hasOutbreak;

	private ViewConfiguration viewConfiguration;
	private final OptionGroup viewModeToggle;
	private final Property.ValueChangeListener viewModeToggleListener;

	protected AbstractCaseView(String viewName) {
		super(viewName);

		if (!ViewModelProviders.of(AbstractCaseView.class).has(ViewConfiguration.class)) {
			// init default view mode
			ViewConfiguration initViewConfiguration = UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS) ? new ViewConfiguration(ViewMode.NORMAL) : new ViewConfiguration(ViewMode.SIMPLE);
			ViewModelProviders.of(AbstractCaseView.class).get(ViewConfiguration.class, initViewConfiguration);
		}

		viewConfiguration = ViewModelProviders.of(AbstractCaseView.class).get(ViewConfiguration.class);

		viewModeToggle = new OptionGroup();
		CssStyles.style(viewModeToggle, 
				ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY,
				CssStyles.VSPACE_TOP_3);
		viewModeToggle.addItems((Object[]) ViewMode.values());
		viewModeToggle.setItemCaption(ViewMode.SIMPLE, I18nProperties.getEnumCaption(ViewMode.SIMPLE));
		viewModeToggle.setItemCaption(ViewMode.NORMAL, I18nProperties.getEnumCaption(ViewMode.NORMAL));
		// View mode toggle is hidden by default
		viewModeToggle.setVisible(false);
		addHeaderComponent(viewModeToggle);     

		viewModeToggleListener = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				viewConfiguration.setViewMode((ViewMode) event.getProperty().getValue());
				// refresh
				ControllerProvider.getCaseController().navigateToCase(getCaseRef().getUuid());
			}
		};        
		viewModeToggle.addValueChangeListener(viewModeToggleListener);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		String[] passedParams = params.split("\\?");
		if (passedParams.length > 0) {
			// Remove possible slash from filters
			String uuid = passedParams[0].replaceAll("/", "");
			caseRef = FacadeProvider.getCaseFacade().getReferenceByUuid(uuid);
		}

		if (caseRef == null) {
			ControllerProvider.getCaseController().navigateToIndex();
			return;
		}
		
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());

		// Handle outbreaks for the disease and district of the case
		if (FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getDistrict(), caze.getDisease()) && caze.getDisease().usesSimpleViewForOutbreaks()) {
			hasOutbreak = true;

			//			viewConfiguration.setViewMode(ViewMode.SIMPLE);
			//			// param might change this
			//			if (passedParams.length > 1 && passedParams[1].startsWith(VIEW_MODE_URL_PREFIX + "=")) {
			//				String viewModeString = passedParams[1].substring(2);
			//				try {
			//					viewConfiguration.setViewMode(ViewMode.valueOf(viewModeString.toUpperCase()));
			//				} catch (IllegalArgumentException ex) { } // just ignore
			//			}
			//
			viewModeToggle.removeValueChangeListener(viewModeToggleListener);
			viewModeToggle.setValue(viewConfiguration.getViewMode());
			viewModeToggle.addValueChangeListener(viewModeToggleListener);
			viewModeToggle.setVisible(true);

		} else {
			hasOutbreak = false;
			viewModeToggle.setVisible(false);
		}

		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, Captions.caseCasesList));
		menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getCaption(CaseDataDto.I18N_PREFIX), params);

		if (!hasOutbreak || !caze.getDisease().usesSimpleViewForOutbreaks() || viewConfiguration.getViewMode() != ViewMode.SIMPLE) {
			menu.addView(CasePersonView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON), params);
			if (caze.getDisease() == Disease.CONGENITAL_RUBELLA) {
				menu.addView(MaternalHistoryView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MATERNAL_HISTORY), params);
			}
			if (!caze.isUnreferredPortHealthCase() && !UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
				menu.addView(HospitalizationView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HOSPITALIZATION), params);
			}
			if (caze.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY && UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
				menu.addView(PortHealthInfoView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PORT_HEALTH_INFO), params);
			}
			menu.addView(CaseSymptomsView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS), params);
			if (caze.getDisease() != Disease.CONGENITAL_RUBELLA) {
				menu.addView(EpiDataView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA), params);
			}
			if (UserProvider.getCurrent().hasUserRight(UserRight.THERAPY_VIEW) && !caze.isUnreferredPortHealthCase()) {
				menu.addView(TherapyView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.THERAPY), params);
			}
			if (UserProvider.getCurrent().hasUserRight(UserRight.CLINICAL_COURSE_VIEW) && !caze.isUnreferredPortHealthCase()) {
				menu.addView(ClinicalCourseView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CLINICAL_COURSE), params);
			}
		}
		if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(caze.getDisease()) && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW) &&
				!caze.isUnreferredPortHealthCase()) {
			menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, Captions.caseContacts), params);
		}

		infoLabel.setValue(caseRef.getCaption());

		infoLabelSub.setValue(caze.getDisease() != Disease.OTHER
				? DataHelper.toStringNullable(caze.getDisease())
						: DataHelper.toStringNullable(caze.getDiseaseDetails()));
	}
	
	@Override
	protected void setSubComponent(Component newComponent) {
		super.setSubComponent(newComponent);
		
		if (caseRef != null && FacadeProvider.getCaseFacade().isDeleted(caseRef.getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}

	public boolean isHasOutbreak() {
		return hasOutbreak;
	}

	public ViewMode getViewMode() {
		if (Boolean.FALSE.equals(hasOutbreak)) {
			return ViewMode.NORMAL;
		}

		return viewConfiguration.getViewMode();
	}
}
