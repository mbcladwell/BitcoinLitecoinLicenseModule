package llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;

public class CryptoCalculator {

  public double btcPriceInDollars;
  public double ltcPriceInDollars;

  public CryptoCalculator() {
    try {
      URL url = new URL("https://api.coinmarketcap.com/v2/ticker/2/");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

      Gson gson = new GsonBuilder().create();
      // TimeUnit.SECONDS.sleep(1);

      LitecoinPriceObject lpo = gson.fromJson(reader, LitecoinPriceObject.class);
      this.ltcPriceInDollars = lpo.data.quotes.USD.price;

    } catch (Exception e) {
      System.out.println("Problems retrieving Litecoin price from coinmarketcap.com!");
      System.out.println(e.toString());
    }
    try {
      URL url = new URL("https://blockchain.info/charts/market-price?format=json");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
      Gson gson = new GsonBuilder().create();

      BitcoinPriceObject bpo = gson.fromJson(reader, BitcoinPriceObject.class);

      this.btcPriceInDollars = bpo.getCurrentValue();
      System.out.println("Current BTC value in dollars: " + this.btcPriceInDollars);

    } catch (Exception e) {
      System.out.println("Problems retrieving Bitcoin price from blockchain.info!");
      System.out.println(e.toString());
    }
  }

  public double getBitcoinPriceInDollars() {
    return this.btcPriceInDollars;
  }

  public double getLitecoinPriceInDollars() {
    return this.ltcPriceInDollars;
  }
}
