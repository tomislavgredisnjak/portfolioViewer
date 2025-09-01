package hr.portfolioviewer;

import android.os.Handler;
import android.os.Looper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataFetcher {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public interface Callback {
        void onResult(BigDecimal price);
        void onError(Exception e);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void getEtf(BigDecimal vwceAmount, BigDecimal fwraAmount, Callback callback) {
        executor.execute(() -> {
            BigDecimal finalPrice = BigDecimal.ZERO;
            String apiKey = "b7a5f1dd120a6e84b84687aed9339dd38e0f2ed2c9f1dd5d8f2d168c6eb88942";

            String urlFwra = "https://serpapi.com/search.json?engine=google_finance&q=FWRA&api_key=" + apiKey;
            String urlVwce = "https://serpapi.com/search.json?engine=google_finance&q=VWCE&api_key=" + apiKey;

            try {
                // Fetch FWRA
                String responseFwra = fetchUrl(urlFwra);
                JsonNode root = objectMapper.readTree(responseFwra);
                JsonNode futuresChain = root.path("futures_chain");

                for (JsonNode stock : futuresChain) {
                    if ("FWRA:BIT".equals(stock.get("stock").asText())) {
                        BigDecimal price = new BigDecimal(stock.get("extracted_price").asText());
                        finalPrice = price.multiply(fwraAmount);
                        break;
                    }
                }

                // Fetch VWCE
                String responseVwce = fetchUrl(urlVwce);
                root = objectMapper.readTree(responseVwce);
                futuresChain = root.path("futures_chain");

                for (JsonNode stock : futuresChain) {
                    if ("VWCE:BIT".equals(stock.get("stock").asText())) {
                        BigDecimal price = new BigDecimal(stock.get("extracted_price").asText());
                        finalPrice = finalPrice.add(price.multiply(vwceAmount));
                        break;
                    }
                }

                BigDecimal result = finalPrice.setScale(2, RoundingMode.HALF_UP);
                handler.post(() -> callback.onResult(result));
            } catch (Exception e) {
                handler.post(() -> callback.onError(e));
            }
        });
    }

    public void getZaba(BigDecimal zabaAmount, Callback callback) {
        executor.execute(() -> {
            String url = "https://zse.hr/hr/papir/310?isin=HRZABARA0009&range=2m";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(json);

                    JsonNode listedSecurities = root.path("ListedSecurities");
                    if (listedSecurities.isArray() && listedSecurities.size() > 0) {
                        BigDecimal result = new BigDecimal(listedSecurities.get(0).path("last_price").asText()).multiply(zabaAmount).setScale(2, RoundingMode.HALF_UP);
                        handler.post(() -> callback.onResult(result));
                    } else {
                        JsonNode webMarketData = root.path("WebMarketData");
                        BigDecimal result = new BigDecimal(webMarketData.get(0).path("last_price").asText()).multiply(zabaAmount).setScale(2, RoundingMode.HALF_UP);
                        handler.post(() -> callback.onResult(result));
                    }
                }
            } catch (IOException e) {
                handler.post(() -> callback.onError(e));
            }
        });
    }

    public void getCrypto(BigDecimal btcAmount, BigDecimal ethAmount, Callback callback) {
        executor.execute(() -> {
            BigDecimal finalPrice = BigDecimal.ZERO;
            String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum&vs_currencies=eur";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(json);

                    JsonNode bitcoin = root.path("bitcoin");
                    JsonNode ethereum = root.path("ethereum");

                    BigDecimal btcPrice = new BigDecimal(bitcoin.path("eur").asText());
                    BigDecimal ethPrice = new BigDecimal(ethereum.path("eur").asText());

                    finalPrice = btcPrice.multiply(btcAmount).add(ethPrice.multiply(ethAmount));
                    BigDecimal result = finalPrice.setScale(2, RoundingMode.HALF_UP);
                    handler.post(() -> callback.onResult(result));
                } else {
                    handler.post(() -> callback.onError(new IOException(response.message())));
                }
            } catch (IOException e) {
                handler.post(() -> callback.onError(e));
            }
        });
    }

    private String fetchUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}
