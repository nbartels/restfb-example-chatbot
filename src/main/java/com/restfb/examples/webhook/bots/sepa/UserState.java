package com.restfb.examples.webhook.bots.sepa;

public enum UserState {
  NO_TRANSACTION, NEW_TRANSACTION_STARTED, WAITING_FOR_AMOUNT, WAITING_FOR_REASON, SUMMARY_READY, WAITING_FOR_SEND_TRANSACTION;
}
