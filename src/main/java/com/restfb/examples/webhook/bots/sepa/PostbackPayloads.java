package com.restfb.examples.webhook.bots.sepa;

interface PostbackPayloads {
  String REQUEST_NEW_TRANSACTION = "REQUEST_NEW_TRANSACTION";
  String REQUEST_VIEW_DEPOT = "REQUEST_VIEW_DEPOT";
  String TRANSACTION_YES = "TRANSACTION_YES";
  String TRANSACTION_NO = "TRANSACTION_NO";
  String REASON_EBAY = "REASON_EBAY";
  String REASON_CUSTOM = "REASON_CUSTOM";
  String REASON_BILLING_NUMBER = "REASON_BILLING_NUMBER";
}
