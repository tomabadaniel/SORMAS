package de.symeda.sormas.ui.caze.maternalhistory;

import java.util.Arrays;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class MaternalHistoryForm extends AbstractEditForm<MaternalHistoryDto> {
	
	private final ViewMode viewMode;
	
	private static final String HTML_LAYOUT =
			LayoutUtil.h3(I18nProperties.getString(Strings.headingMaternalHistory)) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.CHILDREN_NUMBER, MaternalHistoryDto.AGE_AT_BIRTH, "") +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.CONJUNCTIVITIS, MaternalHistoryDto.CONJUNCTIVITIS_ONSET, MaternalHistoryDto.CONJUNCTIVITIS_MONTH) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.MACULOPAPULAR_RASH, MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET, MaternalHistoryDto.MACULOPAPULAR_RASH_MONTH) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.SWOLLEN_LYMPHS, MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET, MaternalHistoryDto.SWOLLEN_LYMPHS_MONTH) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET, MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_MONTH) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.OTHER_COMPLICATIONS, MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET, MaternalHistoryDto.OTHER_COMPLICATIONS_MONTH) +
			LayoutUtil.loc(MaternalHistoryDto.OTHER_COMPLICATIONS_DETAILS) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.RUBELLA, MaternalHistoryDto.RUBELLA_ONSET, "") +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.RASH_EXPOSURE, MaternalHistoryDto.RASH_EXPOSURE_DATE, MaternalHistoryDto.RASH_EXPOSURE_MONTH) +
			LayoutUtil.fluidRowLocs(MaternalHistoryDto.RASH_EXPOSURE_REGION, MaternalHistoryDto.RASH_EXPOSURE_DISTRICT, MaternalHistoryDto.RASH_EXPOSURE_COMMUNITY);
	
	public MaternalHistoryForm(UserRight editOrCreateUserRight, ViewMode viewMode) {
		super(MaternalHistoryDto.class, MaternalHistoryDto.I18N_PREFIX, editOrCreateUserRight);
		this.viewMode = viewMode;
	}
	
	@Override
	protected void addFields() {
		TextField tfChildrenNumber = addField(MaternalHistoryDto.CHILDREN_NUMBER, TextField.class);
		tfChildrenNumber.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfChildrenNumber.getCaption()));
		TextField tfAgeAtBirth = addField(MaternalHistoryDto.AGE_AT_BIRTH, TextField.class);
		tfAgeAtBirth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfAgeAtBirth.getCaption()));
		TextField tfConjunctivitisMonth = addField(MaternalHistoryDto.CONJUNCTIVITIS_MONTH, TextField.class);
		tfConjunctivitisMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfConjunctivitisMonth.getCaption()));
		TextField tfMaculopapularRashMonth = addField(MaternalHistoryDto.MACULOPAPULAR_RASH_MONTH, TextField.class);
		tfMaculopapularRashMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfMaculopapularRashMonth.getCaption()));
		TextField tfSwollenLymphsMonth = addField(MaternalHistoryDto.SWOLLEN_LYMPHS_MONTH, TextField.class);
		tfSwollenLymphsMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfSwollenLymphsMonth.getCaption()));
		TextField tfArthralgiaArthritisMonth = addField(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_MONTH, TextField.class);
		tfArthralgiaArthritisMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfArthralgiaArthritisMonth.getCaption()));
		TextField otherComplicationsMonth = addField(MaternalHistoryDto.OTHER_COMPLICATIONS_MONTH, TextField.class);
		otherComplicationsMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, otherComplicationsMonth.getCaption()));
		TextField rashExposureMonth = addField(MaternalHistoryDto.RASH_EXPOSURE_MONTH, TextField.class);
		rashExposureMonth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, rashExposureMonth.getCaption()));
		
		addFields(MaternalHistoryDto.CONJUNCTIVITIS_ONSET, MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET, MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET,
				MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET, MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET, MaternalHistoryDto.OTHER_COMPLICATIONS_DETAILS,
				MaternalHistoryDto.RUBELLA_ONSET, MaternalHistoryDto.RASH_EXPOSURE_DATE);
		
		addField(MaternalHistoryDto.CONJUNCTIVITIS, OptionGroup.class);
		addField(MaternalHistoryDto.MACULOPAPULAR_RASH, OptionGroup.class);
		addField(MaternalHistoryDto.SWOLLEN_LYMPHS, OptionGroup.class);
		addField(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, OptionGroup.class);
		addField(MaternalHistoryDto.OTHER_COMPLICATIONS, OptionGroup.class);
		addField(MaternalHistoryDto.RUBELLA, OptionGroup.class);
		addField(MaternalHistoryDto.RASH_EXPOSURE, OptionGroup.class);
		
		ComboBox cbRashExposureRegion = addField(MaternalHistoryDto.RASH_EXPOSURE_REGION, ComboBox.class);
		ComboBox cbRashExposureDistrict = addField(MaternalHistoryDto.RASH_EXPOSURE_DISTRICT, ComboBox.class);
		ComboBox cbRashExposureCommunity = addField(MaternalHistoryDto.RASH_EXPOSURE_COMMUNITY, ComboBox.class);

		initializeVisibilitiesAndAllowedVisibilities(null, viewMode);
		
		cbRashExposureRegion.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(cbRashExposureDistrict, region != null ? FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()) : null);
		});
		cbRashExposureDistrict.addValueChangeListener(e -> {
			FieldHelper.removeItems(cbRashExposureCommunity);
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(cbRashExposureCommunity, district != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(district.getUuid()) : null);
		});
		cbRashExposureRegion.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.CONJUNCTIVITIS_ONSET, MaternalHistoryDto.CONJUNCTIVITIS_MONTH), MaternalHistoryDto.CONJUNCTIVITIS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.MACULOPAPULAR_RASH_ONSET, MaternalHistoryDto.MACULOPAPULAR_RASH_MONTH), MaternalHistoryDto.MACULOPAPULAR_RASH, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.SWOLLEN_LYMPHS_ONSET, MaternalHistoryDto.SWOLLEN_LYMPHS_MONTH), MaternalHistoryDto.SWOLLEN_LYMPHS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_ONSET, MaternalHistoryDto.ARTHRALGIA_ARTHRITIS_MONTH), MaternalHistoryDto.ARTHRALGIA_ARTHRITIS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.OTHER_COMPLICATIONS_ONSET, MaternalHistoryDto.OTHER_COMPLICATIONS_MONTH, MaternalHistoryDto.OTHER_COMPLICATIONS_DETAILS), MaternalHistoryDto.OTHER_COMPLICATIONS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.RUBELLA_ONSET), MaternalHistoryDto.RUBELLA, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(MaternalHistoryDto.RASH_EXPOSURE_DATE, MaternalHistoryDto.RASH_EXPOSURE_MONTH, MaternalHistoryDto.RASH_EXPOSURE_REGION, MaternalHistoryDto.RASH_EXPOSURE_DISTRICT, MaternalHistoryDto.RASH_EXPOSURE_COMMUNITY), MaternalHistoryDto.RASH_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
