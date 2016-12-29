package com.restfb.examples.webhook.bots.sepa;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.examples.webhook.Config;
import com.restfb.types.User;
import com.restfb.types.send.*;
import com.restfb.types.webhook.messaging.MessagingItem;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

import java.util.concurrent.ConcurrentHashMap;

public class MessagingHandler {

    private static final ConcurrentHashMap<String, UserState> userStateMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> userName = new ConcurrentHashMap<>();

    private final FacebookClient sendClient = new DefaultFacebookClient(Config.getInstance().getBotTokenAccess(), Version.VERSION_2_7);

    public void handleMessagingItem(MessagingItem item) {
        String senderId = item.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        String userFirstName = getUserName(senderId);


        if (item.getPostback() != null) {
            if (PostbackPayloads.REQUEST_NEW_TRANSACTION.equals(item.getPostback().getPayload())) {
                sendNewTransaction(recipient);
                userStateMap.put(senderId, UserState.NEW_TRANSACTION_STARTED);
                return;
            }
            if (PostbackPayloads.REQUEST_VIEW_DEPOT.equals(item.getPostback().getPayload())) {
                sendNotSupported(recipient, userFirstName);
            }
        }

        if (userStateMap.containsKey(senderId) && !(userStateMap.get(senderId) == UserState.NO_TRANSACTION)) {
            UserState currentUserState = userStateMap.get(senderId);
            if (currentUserState == UserState.NEW_TRANSACTION_STARTED) {
                String iban = item.getMessage().getText();

                try {
                    IbanUtil.validate(iban);
                    Transaction t = new Transaction();
                    t.setIban(item.getMessage().getText());
                    transactionMap.put(senderId, t);
                    sendRequestAmount(recipient);
                    userStateMap.put(senderId, UserState.WAITING_FOR_AMOUNT);
                } catch (IbanFormatException |
                        InvalidCheckDigitException |
                        UnsupportedCountryException e) {
                    sendWrongIban(recipient);
                }
            } else {
                Transaction currentTransaction = transactionMap.get(senderId);
                if (currentUserState == UserState.WAITING_FOR_AMOUNT) {
                    try {
                        currentTransaction.setAmount(Double.valueOf(item.getMessage().getText()));
                        sendRequestReason(recipient);
                        userStateMap.put(senderId, UserState.WAITING_FOR_REASON);
                    } catch (NumberFormatException nfe) {
                        sendWrongAmount(recipient);
                    }
                }
                if (currentUserState == UserState.SUMMARY_READY) {
                    String reason = item.getMessage().getText();
                    currentTransaction.setReasonForPayment(currentTransaction.getReasonForPayment() + reason);
                    sendSummary(currentTransaction, recipient);
                    userStateMap.put(senderId, UserState.WAITING_FOR_SEND_TRANSACTION);
                }
                if (currentUserState == UserState.WAITING_FOR_REASON) {
                    sendRequestAdditionalId(item.getMessage().getQuickReply().getPayload(), recipient);
                    String reason = "";
                    String reasonType = item.getMessage().getQuickReply().getPayload();
                    if (PostbackPayloads.REASON_EBAY.equals(reasonType)) {
                        reason = "Ebay ";
                    }
                    if (PostbackPayloads.REASON_BILLING_NUMBER.equals(reasonType)) {
                        reason = "ReNr ";
                    }
                    currentTransaction.setReasonForPayment(reason);
                    userStateMap.put(senderId, UserState.SUMMARY_READY);
                }
                if (currentUserState == UserState.WAITING_FOR_SEND_TRANSACTION) {
                    if (PostbackPayloads.TRANSACTION_YES.equals(item.getMessage().getQuickReply().getPayload())) {
                        transactionMap.remove(senderId);
                        userStateMap.put(senderId, UserState.NO_TRANSACTION);
                        sendOK(recipient);
                    }
                }

            }
        } else {
            if (item.getMessage() != null
                    && (item.getMessage().getMetadata() == null || item.getMessage().getMetadata().isEmpty())) {
                sendWelcome(item, userFirstName);
            }
        }
    }

    private void fetchUserInfo(String senderId) {
        User sender = sendClient.fetchObject(senderId, User.class, Parameter.with("fields","first_name"));
        userName.put(senderId,sender.getFirstName());
    }

    private String getUserName(String userId) {
        if (userName.get(userId) == null) {
            fetchUserInfo(userId);
        }

        return userName.get(userId);
    }

    private void sendNotSupported(IdMessageRecipient recipient, String userName) {
        Message message = new Message("Es tut mir leid " + userName + ", aber der Service ist momentan nicht verf\u00fcgbar \uD83D\uDE41");
        sendMessage(recipient, message);
    }

    private void sendWrongAmount(IdMessageRecipient recipient) {
        Message message = new Message("Der Betrag ist keine Zahl!\nBitte nenn mir eine passende Zahl \uD83D\uDE20");
        sendMessage(recipient, message);
    }

    private void sendWrongIban(IdMessageRecipient recipient) {
        Message message = new Message("Die IBAN ist nicht okay.\n\u00dcberpr\u00fcf die IBAN und versuche es noch einmal \uD83D\uDE09");
        sendMessage(recipient, message);
    }

    private void sendSummary(Transaction currentTransaction, IdMessageRecipient recipient) {
        Message message = new Message("Diese \u00dcberweisung soll ich wirklich ausf\u00fchren?\n\n" + currentTransaction);
        message.addQuickReply(new QuickReply("Ja", PostbackPayloads.TRANSACTION_YES));
        message.addQuickReply(new QuickReply("Nein", PostbackPayloads.TRANSACTION_NO));
        sendMessage(recipient, message);
    }

    private void sendOK(IdMessageRecipient recipient) {
        Message message = new Message("Die \u00dcberweisung wird nun ausgef\u00fchrt. \uD83D\uDCB8");
        sendMessage(recipient, message);

    }

    private void sendRequestAdditionalId(String payload, IdMessageRecipient recipient) {
        Message message;
        if (PostbackPayloads.REASON_CUSTOM.equals(payload)) {
            message = new Message("Bitte nenne mir einen freien Verwendungszweck");
        } else {
            message = new Message("Bitte gib noch eine zus\u00e4tzliche Nummer an");
        }
        sendMessage(recipient, message);
    }

    private void sendRequestReason(IdMessageRecipient recipient) {
        Message message = new Message("Bitte nenne mir die Art des Verwendungszwecks");
        message.addQuickReply(new QuickReply("Ebay", PostbackPayloads.REASON_EBAY));
        message.addQuickReply(new QuickReply("ReNr", PostbackPayloads.REASON_BILLING_NUMBER));
        message.addQuickReply(new QuickReply("frei", PostbackPayloads.REASON_CUSTOM));
        sendMessage(recipient, message);
    }

    private void sendRequestAmount(IdMessageRecipient recipient) {
        Message message = new Message("Nun brauche ich den Betrag in Euro; als Trennzeichen akzeptiere ich nur '.'");
        sendMessage(recipient, message);

    }

    private void sendNewTransaction(IdMessageRecipient recipient) {
        Message message = new Message("Okay, eine \u00dcberweisung. Dann gib mir bitte die IBAN des Empf\u00e4ngers");
        sendMessage(recipient, message);

    }

    private void sendWelcome(MessagingItem item, String userName) {
        ButtonTemplatePayload payload = new ButtonTemplatePayload("Hallo " + userName +",\n was kann ich heute f\u00fcr dich tun?");
        payload.addButton(new PostbackButton("Neue \u00dcberweisung", PostbackPayloads.REQUEST_NEW_TRANSACTION));
        payload.addButton(new PostbackButton("Depot einsehen", PostbackPayloads.REQUEST_VIEW_DEPOT));
        payload.addButton(new WebButton("Terminanfrage senden","http://www.example.org/contact"));
        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message message = new Message(templateAttachment);
        IdMessageRecipient recipient = new IdMessageRecipient(item.getSender().getId());
        sendMessage(recipient, message);
    }

    private void sendMessage(IdMessageRecipient recipient, Message message) {
        SendResponse resp = sendClient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient),
                Parameter.with("message", message));
    }
}
