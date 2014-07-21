package agi.model;

import java.util.Date;

public class Subscriber {
	private int id;
	private String msisdn;
	private int subscriberType;
	private int status;
	private Date subscriptionDate;
	private Date nextRenewalDate;
	private Date createdDate;
	private Date lastUpdatedDate;
	private Date lastSuccessfulChargingDate;

	public Subscriber(String msisdn) {
		this.msisdn = msisdn;

		this.subscriptionDate = new Date();
		this.nextRenewalDate = new Date();
		this.createdDate = new Date();
		this.lastSuccessfulChargingDate = new Date();
		this.lastUpdatedDate = new Date();
	}
	
	public Subscriber(String msisdn, int subscriberType, int status) {
		this.msisdn = msisdn;
		this.subscriberType = subscriberType;
		this.status = status;

		this.subscriptionDate = new Date();
		this.nextRenewalDate = new Date();
		this.createdDate = new Date();
		this.lastSuccessfulChargingDate = new Date();
		this.lastUpdatedDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public int getSubscriberType() {
		return subscriberType;
	}

	public void setSubscriberType(int subscriberType) {
		this.subscriberType = subscriberType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getNextRenewalDate() {
		return nextRenewalDate;
	}

	public void setNextRenewalDate(Date nextRenewalDate) {
		this.nextRenewalDate = nextRenewalDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Date getLastSuccessfulChargingDate() {
		return lastSuccessfulChargingDate;
	}

	public void setLastSuccessfulChargingDate(Date lastSuccessfulChargingDate) {
		this.lastSuccessfulChargingDate = lastSuccessfulChargingDate;
	}

	@Override
	public String toString() {
		return "Subscriber [msisdn=" + msisdn + "]";
	}
}