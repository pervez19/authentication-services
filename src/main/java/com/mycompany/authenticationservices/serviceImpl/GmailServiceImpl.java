package com.mycompany.authenticationservices.serviceImpl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;
import static javax.mail.Message.RecipientType.TO;

@Service
public class GmailServiceImpl {
    private static final String FROM_EMAIL = "wasikfarhan74@gmail.com";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private final Gmail service;
    private static final String CLIENT_SECRET = "/gmail/client_secret_922644995789-cuk9kpa1a1l9635885qjjf1rou1rtupt.apps.googleusercontent.com.json";

    public GmailServiceImpl() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("My Project for Email")
                .build();
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(GmailServiceImpl.class.getResourceAsStream(CLIENT_SECRET)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8081).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void sendMail(String subject, String message, String to) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(FROM_EMAIL));
        email.addRecipient(TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setReplyTo(new InternetAddress[]{new InternetAddress(FROM_EMAIL)});
        email.setText(message);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message msg = new Message();
        msg.setRaw(encodedEmail);

        try {
            msg = service.users().messages().send("me", msg).execute();
            System.out.println("Message id: " + msg.getId());
            System.out.println(msg.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public void receivedMail() throws IOException {
        String TOPIC_NAME = "projects/my-project-for-email-393303/topics/MyTopic";
        String SUBSCRIPTION_NAME="projects/my-project-for-email-393303/subscriptions/MySub";
        String PROJECT_ID="my-project-for-email-393303";
        createTopicExample(PROJECT_ID, TOPIC_NAME);
        createPullSubscriptionExample(
                TOPIC_NAME, SUBSCRIPTION_NAME, TOPIC_NAME);
        subscribeAsyncExample(TOPIC_NAME, SUBSCRIPTION_NAME);
    }


    private void createTopicExample(String projectId, String topicId) throws IOException {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            TopicName topicName = TopicName.of(projectId, topicId);
            Topic topic = topicAdminClient.createTopic(topicName);
            System.out.println("Created topic: " + topic.getName());

        }
    }
    private void deleteTopicExample(String projectId, String topicId) throws IOException {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            TopicName topicName = TopicName.of(projectId, topicId);
            try {
                topicAdminClient.deleteTopic(topicName);
                System.out.println("Deleted topic.");
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void createPullSubscriptionExample(
            String projectId, String subscriptionId, String topicId) throws IOException {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            TopicName topicName = TopicName.of(projectId, topicId);
            SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
            // Create a pull subscription with default acknowledgement deadline of 10 seconds.
            // Messages not successfully acknowledged within 10 seconds will get resent by the server.
            Subscription subscription =
                    subscriptionAdminClient.createSubscription(
                            subscriptionName, topicName, PushConfig.getDefaultInstance(), 10);
            System.out.println("Created pull subscription: " + subscription.getName());
        }
    }

    public static void updatePushConfigurationExample(
            String projectId, String subscriptionId, String pushEndpoint) throws IOException {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
            PushConfig pushConfig = PushConfig.newBuilder().setPushEndpoint(pushEndpoint).build();
            subscriptionAdminClient.modifyPushConfig(subscriptionName, pushConfig);
            Subscription subscription = subscriptionAdminClient.getSubscription(subscriptionName);
            System.out.println(
                    "Updated push endpoint to: " + subscription.getPushConfig().getPushEndpoint());
        }
    }

    private void deleteSubscriptionExample(String projectId, String subscriptionId)
            throws IOException {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
            try {
                subscriptionAdminClient.deleteSubscription(subscriptionName);
                System.out.println("Deleted subscription.");
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void subscribeAsyncExample(String projectId, String subscriptionId) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            // Start the subscriber.
            subscriber.startAsync().awaitRunning();
            System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
    }
}


