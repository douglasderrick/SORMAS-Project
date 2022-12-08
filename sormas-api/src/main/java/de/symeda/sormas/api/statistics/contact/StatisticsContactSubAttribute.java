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
package de.symeda.sormas.api.statistics.contact;

import java.util.Collection;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;
import de.symeda.sormas.api.statistics.caze.StatisticsCaseSubAttribute;

public class StatisticsContactSubAttribute {
	
	@SuppressWarnings("rawtypes")
	private final Enum _enum;	
	private StatisticsContactSubAttributeEnum baseEnum;
	
	private boolean usedForFilters;
	private boolean usedForGrouping;
	private IValuesGetter valuesGetter;
	
	
	@SuppressWarnings("rawtypes")
	public StatisticsContactSubAttribute(Enum _enum, StatisticsContactSubAttributeEnum baseEnum, boolean usedForFilters, boolean usedForGrouping, IValuesGetter valuesGetter) {
		this._enum = _enum;
		this.baseEnum = baseEnum;
		this.valuesGetter = valuesGetter;
		
		this.usedForFilters = usedForFilters;
		this.usedForGrouping = usedForGrouping;
	}
	
	

	public String toString() {
		return I18nProperties.getEnumCaption(_enum != null ? _enum : baseEnum);
	}

	public boolean isUsedForFilters() {
		return usedForFilters;
	}	
	
	public boolean isUsedForGrouping() {
		return usedForGrouping;
	}
	
//	public StatisticsSubAttributeEnum getBaseEnum() {
//		return baseEnum;
//	}
	
	public StatisticsContactSubAttributeEnum getBaseEnum() {
		return baseEnum;
	}
	
	@SuppressWarnings("rawtypes")
	public static Enum getEnum (StatisticsContactSubAttribute attribute) {
		return attribute == null ? null : attribute._enum;
	}
	
	public static StatisticsContactSubAttributeEnum getBaseEnum (StatisticsContactSubAttribute attribute) {
		return attribute == null ? null : attribute.getBaseEnum();
	}
	
	public Collection<? extends StatisticsGroupingKey> getValues (StatisticsAttribute attribute) {
		return valuesGetter.get(this, attribute);
	}
	
	public interface IValuesGetter {
		Collection<? extends StatisticsGroupingKey> get(StatisticsContactSubAttribute subAttribute, StatisticsAttribute attribute);
	}
}