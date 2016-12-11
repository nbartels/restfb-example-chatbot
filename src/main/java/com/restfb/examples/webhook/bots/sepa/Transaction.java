package com.restfb.examples.webhook.bots.sepa;

public class Transaction {

  private String iban;

  private double amount;

  private String reasonForPayment;

  public String getIban() {
    return iban;
  }

  public double getAmount() {
    return amount;
  }

  public String getReasonForPayment() {
    return reasonForPayment;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public void setReasonForPayment(String reasonForPayment) {
    this.reasonForPayment = reasonForPayment;
  }

  @Override
  public String toString() {
    return "IBAN: " + iban + "\nBetrag: " + amount + "\u20AC\nVerw.Zweck: " + reasonForPayment;
  }
}
