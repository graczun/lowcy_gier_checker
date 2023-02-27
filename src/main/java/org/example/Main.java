package org.example;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main {

    static int articles = 0;
    static String mailFrom;
    static String passwordFrom;
    static String mailTo;
    static String searchPhrase;

    public static void main(String[] args) {
        mailFrom = args[0];
        passwordFrom = args[1];
        mailTo = args[2];
        searchPhrase = args[3];
        ScheduledExecutorService ses = newSingleThreadScheduledExecutor();
        long millis = 60 * 1000;
        Runnable checkTask = generateCheckTask();
        ses.scheduleAtFixedRate(checkTask, 0, millis, MILLISECONDS);
    }

    private static Runnable generateCheckTask() {
        return () -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                if (now.getHour() < 8 || now.getHour() > 22) {
                    System.out.println(LocalDateTime.now() + " Skipping check, it's night");
                    return;
                }
                System.out.println(LocalDateTime.now() + " Checking if " + searchPhrase + " is available... Previously found " + articles + " articles");
                String content = makeGetHttpRequestTo("https://lowcygier.pl/?s=" + searchPhrase);
                int newArticles = getNumberOfArticles(content);
                if (newArticles > articles) {
                    System.out.println(searchPhrase + " is available!");
                    sendMailWithInformation(searchPhrase + " is available!", searchPhrase + " is available!\n https://lowcygier.pl/?s=" + searchPhrase);
                }
                articles = newArticles;
            } catch (Throwable t) {
                System.err.println("Error while checking if " + searchPhrase + " is available");
                t.printStackTrace();
                System.err.println("Sending mail with information");
                sendMailWithInformation(t.getMessage(), t.toString());
                System.err.println("Exiting 1");
                System.exit(1);
            }
        };
    }

    private static void sendMailWithInformation(String subject, String content) {
        Mailer mailer = new Mailer("smtp.gmail.com", 587, Main.mailFrom, Main.passwordFrom, TransportStrategy.SMTP_TLS);
        mailer.sendMail(new EmailBuilder()
                .from(Main.mailFrom, Main.mailFrom)
                .to(Main.mailTo)
                .subject(subject)
                .text(content)
                .build());
    }

    //get content of web page
    public static String makeGetHttpRequestTo(String url) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                return EntityUtils.toString(entity);
            }
        }
        return null;
    }

    private static int getNumberOfArticles(String content) {
        return Integer.parseInt(content.substring(content.indexOf("Znaleziono") + "Znaleziono".length(), content.indexOf("wpisów")).trim());
    }
}