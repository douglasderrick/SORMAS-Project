package de.symeda.sormas.api.statistics.contact;


import java.io.Serializable;
import java.math.BigDecimal;

import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.utils.DataHelper;

//public class StatisticsCountDto implements Serializable {
	
public class StatisticsContactCountDto implements Serializable {

	private static final long serialVersionUID = 8900419282951754875L;

	private Integer caseCount;
	private Integer population;
	private StatisticsGroupingKey rowKey;
	private StatisticsGroupingKey columnKey;
	
	//public StatisticsCountDto(Integer caseCount, Integer population, StatisticsGroupingKey rowKey, StatisticsGroupingKey columnKey) {

	public StatisticsContactCountDto(Integer caseCount, Integer population, StatisticsGroupingKey rowKey, StatisticsGroupingKey columnKey) {

		super();
		this.caseCount = caseCount;
		this.population = population;
		this.rowKey = rowKey;
		this.columnKey = columnKey;
	}
	
//	public Integer getCount() {
//		return caseCount;
//	}
//	public void setCount(Integer caseCount) {
//		this.caseCount = caseCount;
//	}

	public Integer getContactCount() {
		return caseCount;
	}

	public void setContactCount(Integer caseCount) {
		this.caseCount = caseCount;
	}

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	public StatisticsGroupingKey getRowKey() {
		return rowKey;
	}

	public void setRowKey(StatisticsGroupingKey rowKey) {
		this.rowKey = rowKey;
	}

	public StatisticsGroupingKey getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(StatisticsGroupingKey columnKey) {
		this.columnKey = columnKey;
	}

	public BigDecimal getIncidence(int divisor) {

		if (population == null) {
			return null;
		}
		if (caseCount == null || caseCount.intValue() == 0) {
			return BigDecimal.ZERO;
		}
		
		//return InfrastructureHelper.getContactIncidence(count.intValue(), population.intValue(), divisor);

		return InfrastructureHelper.getContactIncidence(caseCount.intValue(), population.intValue(), divisor);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatisticsContactCountDto) {
			StatisticsContactCountDto other = (StatisticsContactCountDto)obj;
			return DataHelper.equal(rowKey, other.rowKey) && DataHelper.equal(columnKey, other.columnKey);

		}

		if (obj instanceof StatisticsContactCountDto) {
			StatisticsContactCountDto other = (StatisticsContactCountDto) obj;
			return DataHelper.equal(rowKey, other.rowKey) && DataHelper.equal(columnKey, other.columnKey);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {

		int prime = 31;
		int result = 1;
		result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
		result = prime * result + ((columnKey == null) ? 0 : columnKey.hashCode());
		return result;
	}

	public Object getCaseCount() {
		// TODO Auto-generated method stub
		return null;
	}
}

