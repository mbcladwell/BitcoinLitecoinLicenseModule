package bllm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.util.logging.*;

public class CryptoCalculator {

  public double btcPriceInDollars;
  public double ltcPriceInDollars;

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public CryptoCalculator() {
    try {
      URL url = new URL("https://api.coinmarketcap.com/v2/ticker/2/");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

      Gson gson = new GsonBuilder().create();
      // TimeUnit.SECONDS.sleep(1);

      LitecoinPriceObject lpo = gson.fromJson(reader, LitecoinPriceObject.class);
      this.ltcPriceInDollars = lpo.data.quotes.USD.price;
      LOGGER.info("Current LTC value in dollars: " + this.ltcPriceInDollars);

    } catch (Exception e) {
      LOGGER.severe("Problems retrieving Litecoin price from coinmarketcap.com!");
      LOGGER.severe(e.toString());
    }
    try {
      URL url = new URL("https://blockchain.info/charts/market-price?format=json");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
      Gson gson = new GsonBuilder().create();

      BitcoinPriceObject bpo = gson.fromJson(reader, BitcoinPriceObject.class);

      this.btcPriceInDollars = bpo.getCurrentValue();
      LOGGER.info("Current BTC value in dollars: " + this.btcPriceInDollars);

    } catch (Exception e) {
      LOGGER.severe("Problems retrieving Bitcoin price from blockchain.info!");
      LOGGER.severe(e.toString());
    }
  }

  public double getBitcoinPriceInDollars() {
    return this.btcPriceInDollars;
  }

  public double getLitecoinPriceInDollars() {
    return this.ltcPriceInDollars;
  }
}
