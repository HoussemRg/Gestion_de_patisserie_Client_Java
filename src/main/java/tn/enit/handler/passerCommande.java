package tn.enit.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;

public class passerCommande implements JobHandler  {
    private static final String MESSAGE_NAME = "msg_demandematiereenvoye";
    //ClientNotifier clientNotifierService = new ClientNotifier();
    private static final String ZEEBE_ADDRESS = "00b68edd-1f87-4112-a0a3-e9e8bd5e5cda.lhr-1.zeebe.camunda.io:443";
    private static final String ZEEBE_CLIENT_ID = "KU2luBMzrJYXq48ir7.37I0xgnT3J7Bi";
    private static final String ZEEBE_CLIENT_SECRET = "FG~1LF~4ybFTIHF4-tx2YeW2JSzWfAcW5C2-MMspvMutlzeaCAvuLUuFmm0aIE0.";
    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
    private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {

        final String travelRequestId2 = "123456789";
        final OAuthCredentialsProvider credentialsProvider =
                new OAuthCredentialsProviderBuilder()
                        .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
                        .audience(ZEEBE_TOKEN_AUDIENCE)
                        .clientId(ZEEBE_CLIENT_ID)
                        .clientSecret(ZEEBE_CLIENT_SECRET)
                        .build();

        try (final ZeebeClient travelAgencyClient = ZeebeClient.newClientBuilder()
                .gatewayAddress(ZEEBE_ADDRESS)
                .credentialsProvider(credentialsProvider)
                .build()) {


            //Build the Message Variables


            travelAgencyClient.newPublishMessageCommand()
                    .messageName(MESSAGE_NAME)
                    .correlationKey(travelRequestId2)
                    .send()
                    .join();

            System.out.println(travelRequestId2 + " message de commande de matiere premiere  envoyé");

            //Complete the Job
            client.newCompleteCommand(job.getKey()).send().join();
        }


    }
}
