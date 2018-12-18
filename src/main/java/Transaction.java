package bllm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.logging.*;

public class Transaction {

  private long numberOfConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;
  private long deltaTime;
  private long paymentInSatoshis = 0; // value of LiteCoin Transaction in Bitcoin!!
  private String merchantWalletID;
  private String[] addresses;
  private boolean walletIDnotFound = false;
  private URL url;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public TransactionObject.Output[] outputs;

  /**
   * Create a LiteCoin Transaction Object. This object will provide access to transaction
   * parameters, which will be used to validate the license. The lto provides the transaction value
   * in Satoshis, which are converted to Bitcoin by dividing by 1e8. a transaction:
   * https://api.blockcypher.com/v1/ltc/main/txs/9e9c462b755defda988e9950b9797cecd055a017d32d9c6feff89ebdea5fe3cd
   * my wallet: LMGaFd8tmxJSDbo3GLZFJaPd1kNJ9r1v48
   */
  public Transaction(
      String transactionID, String merchantWalletID, String unitsOfRequestedPayment) {
    try {
      switch (unitsOfRequestedPayment) {
        case "Litecoin":
          url = new URL("https://api.blockcypher.com/v1/ltc/main/txs/" + transactionID);
          LOGGER.info("URL: " + url);

          break;
        case "Bitcoin":
          url = new URL("https://api.blockcypher.com/v1/btc/main/txs/" + transactionID);
          break;
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
      LOGGER.info("post bufferedreader ");
      Gson gson = new GsonBuilder().create();
      LOGGER.info("post gson");
      TransactionObject to = gson.fromJson(reader, TransactionObject.class);
      LOGGER.info("Transaction object: " + to);

      this.merchantWalletID = merchantWalletID;
      this.numberOfConfirmations = to.getConfirmations();
      this.doubleSpend = to.getDoubleSpend();
      Instant instant = Instant.parse(to.getTransactionDate());
      LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
      this.transactionDate = ldt.toLocalDate();
      this.deltaTime = Duration.between(instant, Instant.now()).toHours();

      outputs = to.getOutputs();

      for (int i = 0; i <= outputs.length - 1; i++) {
        try {
          addresses = outputs[i].getAddresses();

          for (int j = 0; j <= addresses.length - 1; j++) {
            try {
              LOGGER.info("address: " + addresses[j]);
              if (this.merchantWalletID.equals(addresses[j])) {
                this.paymentInSatoshis = outputs[i].getValue();
              } else { // wallet not part of transaction
                if (j == addresses.length - 1 && this.paymentInSatoshis == 0) {
                  this.walletIDnotFound = true;
                }
              }
            } catch (NullPointerException e) {
            }
          }
        } catch (NullPointerException e) {
        }
      }
      if (this.paymentInSatoshis == 0) {}

    } catch (Exception e) {
      LOGGER.severe("Problems retrieving transaction ID: " + transactionID);
      LOGGER.severe(e.toString());
    }
  }

  public long getNumberOfConfirmations() {
    return this.numberOfConfirmations;
  }

  public boolean getDoubleSpend() {
    return this.doubleSpend;
  }

  public LocalDate getTransactionDate() {
    return this.transactionDate;
  }

  public long getPaymentInSatoshis() {
    return this.paymentInSatoshis;
  }
  /** Time in hours between now and transaction execution */
  public long getDeltaTime() {
    return this.deltaTime;
  }

  public boolean getWalletIDnotFound() {
    return this.walletIDnotFound;
  }
}
