package llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.time.*;

public class Transaction {

  private long numberOfConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;
  private long deltaTime;
  private long paymentInSatoshis = 0; // value of LiteCoin Transaction
  private String merchantWalletID;
  private String[] addresses;
  private boolean walletIDnotFound = false;

  public LitecoinTransactionObject.Output[] outputs;

  /**
   * Create a LiteCoin Transaction Object. This object will provide access to transaction
   * parameters, which will be used to validate the license. The lto provides the transaction value
   * in Satoshis, which are converted to Bitcoin by dividing by 1e8. a transaction:
   * https://api.blockcypher.com/v1/ltc/main/txs/9e9c462b755defda988e9950b9797cecd055a017d32d9c6feff89ebdea5fe3cd
   * my wallet: LMGaFd8tmxJSDbo3GLZFJaPd1kNJ9r1v48
   */
  public Transaction(String transactionID, String merchantWalletID) {
    try {
      URL url = new URL("https://api.blockcypher.com/v1/ltc/main/txs/" + transactionID);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
      Gson gson = new GsonBuilder().create();
      LitecoinTransactionObject lto = gson.fromJson(reader, LitecoinTransactionObject.class);

      this.merchantWalletID = merchantWalletID;
      this.numberOfConfirmations = lto.getConfirmations();
      this.doubleSpend = lto.getDoubleSpend();
      Instant instant = Instant.parse(lto.getTransactionDate());
      LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
      this.transactionDate = ldt.toLocalDate();
      this.deltaTime = Duration.between(instant, Instant.now()).toHours();

      outputs = lto.getOutputs();

      for (int i = 0; i <= outputs.length - 1; i++) {
        try {
          addresses = outputs[i].getAddresses();

          for (int j = 0; j <= addresses.length - 1; j++) {
            try {
              System.out.println("address: " + addresses[j]);
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
      System.out.println("Problems retrieving transaction ID: " + transactionID);
      System.out.println(e.toString());
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
    System.out.println("in getter  " + this.paymentInSatoshis);
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
