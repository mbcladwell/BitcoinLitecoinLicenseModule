package llm;

import java.io.*;

/**
 * https://timboudreau.com/blog/json/read https://api.coinmarketcap.com/v2/ticker/2/ caution must be
 * USD (not uSD so must change USD to USd and uSD to USD)
 */
public final class LitecoinPriceObject {
  public final Data data;
  public final Metadata metadata;

  public LitecoinPriceObject(Data data, Metadata metadata) {
    this.data = data;
    this.metadata = metadata;
  }

  public static final class Data {
    public final long id;
    public final String name;
    public final String symbol;
    public final String website_slug;
    public final long rank;
    public final long circulating_supply;
    public final long total_supply;
    public final long max_supply;
    public final Quotes quotes;
    public final long last_updated;

    public Data(
        long id,
        String name,
        String symbol,
        String website_slug,
        long rank,
        long circulating_supply,
        long total_supply,
        long max_supply,
        Quotes quotes,
        long last_updated) {
      this.id = id;
      this.name = name;
      this.symbol = symbol;
      this.website_slug = website_slug;
      this.rank = rank;
      this.circulating_supply = circulating_supply;
      this.total_supply = total_supply;
      this.max_supply = max_supply;
      this.quotes = quotes;
      this.last_updated = last_updated;
    }

    public static final class Quotes {
      public final USd USD;

      public Quotes(USd USD) {
        this.USD = USD;
      }

      public static final class USd {
        public final double price;
        public final double volume_24h;
        public final long market_cap;
        public final double percent_change_1h;
        public final double percent_change_24h;
        public final double percent_change_7d;

        public USd(
            double price,
            double volume_24h,
            long market_cap,
            double percent_change_1h,
            double percent_change_24h,
            double percent_change_7d) {
          this.price = price;
          this.volume_24h = volume_24h;
          this.market_cap = market_cap;
          this.percent_change_1h = percent_change_1h;
          this.percent_change_24h = percent_change_24h;
          this.percent_change_7d = percent_change_7d;
        }
      }
    }
  }

  public static final class Metadata {
    public final long timestamp;
    public final Error error;

    public Metadata(long timestamp, Error error) {
      this.timestamp = timestamp;
      this.error = error;
    }

    public static final class Error {

      public Error() {}
    }
  }
}
