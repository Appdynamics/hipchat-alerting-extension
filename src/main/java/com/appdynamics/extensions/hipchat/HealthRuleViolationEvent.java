package com.appdynamics.extensions.hipchat;

import java.util.List;

/**
 * @author ashish mehta
 *
 */
public class HealthRuleViolationEvent extends Event {
	
	private String hrvAlertTime;
	private String healthRuleName;
	private String healthRuleID;
	private String hrvTimePeriodInMinutes;
	private String affectedEntityType;
	private String affectedEntityName;
	private String affectedEntityID;
	private List<EvaluationEntity> evaluationEntity;
	private String summaryMessage;
	private String incidentID;
	private String eventType;


	public String getHealthRuleName() {
		return healthRuleName;
	}

	public void setHealthRuleName(String healthRuleName) {
		this.healthRuleName = healthRuleName;
	}

	public String getHealthRuleID() {
		return healthRuleID;
	}

	public void setHealthRuleID(String healthRuleID) {
		this.healthRuleID = healthRuleID;
	}

	public String getHrvAlertTime() {
		return hrvAlertTime;
	}

	public void setHrvAlertTime(String hrvAlertTime) {
		this.hrvAlertTime = hrvAlertTime;
	}

	public String getHrvTimePeriodInMinutes() {
		return hrvTimePeriodInMinutes;
	}

	public void setHrvTimePeriodInMinutes(String hrvTimePeriodInMinutes) {
		this.hrvTimePeriodInMinutes = hrvTimePeriodInMinutes;
	}

	public String getAffectedEntityType() {
		return affectedEntityType;
	}

	public void setAffectedEntityType(String affectedEntityType) {
		this.affectedEntityType = affectedEntityType;
	}

	public String getAffectedEntityName() {
		return affectedEntityName;
	}

	public void setAffectedEntityName(String affectedEntityName) {
		this.affectedEntityName = affectedEntityName;
	}

	public String getAffectedEntityID() {
		return affectedEntityID;
	}

	public void setAffectedEntityID(String affectedEntityID) {
		this.affectedEntityID = affectedEntityID;
	}

	public List<EvaluationEntity> getEvaluationEntity() {
		return evaluationEntity;
	}

	public void setEvaluationEntity(List<EvaluationEntity> evaluationEntity) {
		this.evaluationEntity = evaluationEntity;
	}

	public String getSummaryMessage() {
		return summaryMessage;
	}

	public void setSummaryMessage(String summaryMessage) {
		this.summaryMessage = summaryMessage;
	}

	public String getIncidentID() {
		return incidentID;
	}

	public void setIncidentID(String incidentID) {
		this.incidentID = incidentID;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public class EvaluationEntity {

		private String type;
		private String name;
		private String id;
		private List<String> triggeredCondition;

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<String> getTriggeredCondition() {
			return triggeredCondition;
		}
		public void setTriggeredCondition(List<String> triggeredCondition) {
			this.triggeredCondition = triggeredCondition;
		}

	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Health rule violation = [").append("P:").append(super.getPriority()).append(", ");
		sb.append("Severity:").append(super.getSeverity()).append(", ");
		sb.append("App Name:").append(super.getAppName()).append(", ");
		sb.append("Health rule name:").append(this.healthRuleName).append(", ");
		sb.append("Affected Entity Type:").append(this.getAffectedEntityType()).append(", ");
		sb.append("Affected Entity Name:").append(this.getAffectedEntityName()).append(", ");
		sb.append("URL:").append(this.getDeepLinkUrl()).append(this.getIncidentID()).append("]");
		
		return sb.toString();
	}

}
