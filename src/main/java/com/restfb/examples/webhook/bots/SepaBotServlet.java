/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restfb.examples.webhook.bots;

import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.examples.webhook.bots.base.AbstractFacebookBotServlet;
import com.restfb.examples.webhook.bots.sepa.MessagingHandler;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import org.eclipse.jetty.util.log.Log;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SepaBotServlet extends AbstractFacebookBotServlet {

    MessagingHandler handler = new MessagingHandler();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JsonMapper mapper = new DefaultJsonMapper();
        WebhookObject whObject = mapper.toJavaObject(body, WebhookObject.class);

        Log.getLog().info("Sepa bot: \n" + body);

        for (final WebhookEntry entry : whObject.getEntryList()) {
            for (final MessagingItem item : entry.getMessaging()) {
                handler.handleMessagingItem(item);
            }
        }
    }



}
