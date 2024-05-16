package tn.enit;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import tn.enit.handler.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EnvoyerCommandeMain {
    private static final String ZEEBE_ADDRESS = "00b68edd-1f87-4112-a0a3-e9e8bd5e5cda.lhr-1.zeebe.camunda.io:443";
    private static final String ZEEBE_CLIENT_ID = "KU2luBMzrJYXq48ir7.37I0xgnT3J7Bi";
    private static final String ZEEBE_CLIENT_SECRET = "FG~1LF~4ybFTIHF4-tx2YeW2JSzWfAcW5C2-MMspvMutlzeaCAvuLUuFmm0aIE0.";
    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
    private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";
    private static final String DEMANDE_DE_RESERVATION_JOB_TYPE = "envoyerCommande";
    private static final String NOTIFIER_CLIENT_JOB_TYPE = "notifierClient";
    private static final String INFORMER_CLIENT_JOB_TYPE = "informerClient";
    private static final String PASSER_COMMANDE_JOB_TYPE = "envoyerCommande";

    public static void main(String[] args) {

        //final Map<String, Object> variables = new HashMap<String, Object>();
        //variables.put("num_carte_de_credit", "C8_12345");



        final OAuthCredentialsProvider credentialsProvider =
                new OAuthCredentialsProviderBuilder()
                        .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
                        .audience(ZEEBE_TOKEN_AUDIENCE)
                        .clientId(ZEEBE_CLIENT_ID)
                        .clientSecret(ZEEBE_CLIENT_SECRET)
                        .build();

        try (final ZeebeClient client =
                     ZeebeClient.newClientBuilder()
                             .gatewayAddress(ZEEBE_ADDRESS)
                             .credentialsProvider(credentialsProvider)
                             .build()) {
            System.out.println("Connected to: " + client.newTopologyRequest().send().join());
            //  client.newCreateInstanceCommand()
            //           .bpmnProcessId("process_assurance")
            //           .latestVersion()
            //           .variables(variables)
            //           .send()
            //           .join();

            //Thread.sleep(10000);
            final JobWorker envoyerCommande = client.newWorker()
                    .jobType(DEMANDE_DE_RESERVATION_JOB_TYPE)
                    .handler(new EnvoyerCommandeHandler())
                    .timeout(Duration.ofSeconds(10).toMillis())
                    .open();

            final JobWorker notifierClient =
                    client.newWorker()
                            .jobType(NOTIFIER_CLIENT_JOB_TYPE)
                            .handler(new ClientNotifierHandler())
                            .timeout(Duration.ofSeconds(10).toMillis())
                            .open();

            final JobWorker informerClient =
                    client.newWorker()
                            .jobType(INFORMER_CLIENT_JOB_TYPE)
                            .handler(new InformerClientHandler())
                            .timeout(Duration.ofSeconds(10).toMillis())
                            .open();

            final JobWorker passerCommande =
                    client.newWorker()
                            .jobType(PASSER_COMMANDE_JOB_TYPE)
                            .handler(new passerCommande())
                            .timeout(Duration.ofSeconds(10).toMillis())
                            .open();
            Thread.sleep(10000);

            //Wait for the Workers
            Scanner sc = new Scanner(System.in);
            sc.nextInt();
            sc.close();
            envoyerCommande.close();
            notifierClient.close();
            informerClient.close();
            passerCommande.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
