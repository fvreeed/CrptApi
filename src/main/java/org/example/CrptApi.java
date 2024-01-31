package org.example;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {

    private final TimeUnit timeUnit;

    private final int requestLimit;

    private final Lock lock = new ReentrantLock();

    private long lastRequestTime;

    private int requestCount;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.lastRequestTime = System.currentTimeMillis();
        this.requestCount = 0;
    }

    @Getter
    @Setter
    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private Product[] products;
        private String reg_date;
        private String reg_number;
    }

    @Getter
    @Setter
    public static class Description {
        private String participantInn;
    }

    @Getter
    @Setter
    public static class Product {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;
    }

    public void createDocument(Document document) {
        try {

            lock.lock();

            long currentTime = System.currentTimeMillis();
            long elapsedTime = timeUnit.toMillis(1);

            if (currentTime - lastRequestTime >= elapsedTime) {
                requestCount = 0;
                lastRequestTime = currentTime;
            }

            if (requestCount < requestLimit) {
                HttpClient httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");

                String documentJson = new Gson().toJson(document);

                httpPost.setHeader("Content-Type", "application/json");

                httpPost.setEntity(new StringEntity(documentJson));

                HttpResponse response = httpClient.execute(httpPost);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    System.out.println("Запрос успешно выполнен");
                } else {
                    System.out.println("Произошла ошибка. Код статуса: " + statusCode);
                }

                requestCount++;
            } else {
                System.out.println("Вы превысили число запросов!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5);

        Document document = new Document();

        Description description = new Description();
        description.setParticipantInn("string");
        document.setDescription(description);

        document.setDoc_id("string");
        document.setDoc_status("string");
        document.setDoc_type("LP_INTRODUCE_GOODS");
        document.setImportRequest(true);
        document.setOwner_inn("string");
        document.setParticipant_inn("string");
        document.setProducer_inn("string");
        document.setProduction_date("2020-01-23");
        document.setProduction_type("string");

        Product product = new Product();
        product.setCertificate_document("string");
        product.setCertificate_document_date("2020-01-23");
        product.setCertificate_document_number("string");
        product.setOwner_inn("string");
        product.setProducer_inn("string");
        product.setProduction_date("2020-01-23");
        product.setTnved_code("string");
        product.setUit_code("string");
        product.setUitu_code("string");

        Product[] products = {product};
        document.setProducts(products);

        document.setReg_date("2020-01-23");
        document.setReg_number("string");

        crptApi.createDocument(document);
    }
}
