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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

public class RegionsView extends AbstractConfigurationView {

	private static final long serialVersionUID = -3487830069266335042L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/regions";

	private RegionCriteria criteria;

	// Filter
	private TextField searchField;
	private Button resetButton;

	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private RegionsGrid grid;	
	protected Button createButton;

	public RegionsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(RegionsView.class).get(RegionCriteria.class);

		grid = new RegionsGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			Button exportButton = new Button(I18nProperties.getCaption(Captions.export));
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(VaadinIcons.TABLE);
			addHeaderComponent(exportButton);

			StreamResource streamResource = new GridExportStreamResource(grid, "sormas_regions", "sormas_regions_" + DateHelper.formatDateForExport(new Date()) + ".csv", RegionsGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = new Button(I18nProperties.getCaption(Captions.actionNewEntry));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(
					e -> ControllerProvider.getInfrastructureController().createRegion());
			addHeaderComponent(createButton);
		}

		addComponent(gridLayout);
	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptSearch));
		searchField.setNullRepresentation("");
		searchField.addTextChangeListener(e -> {
			criteria.nameEpidLike(e.getText());
			navigateTo(criteria);
		});
		CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
		filterLayout.addComponent(searchField);

		resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		resetButton.setVisible(false);
		CssStyles.style(resetButton, CssStyles.FORCE_CAPTION);
		resetButton.addClickListener(event -> {
			ViewModelProviders.of(RegionsView.class).remove(RegionCriteria.class);
			navigateTo(null);
		});
		filterLayout.addComponent(resetButton);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		searchField.setValue(criteria.getNameEpidLike());

		applyingCriteria = false;
	}

}
