package bllm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TransactionTest {

  private long numberOfConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;
  private long paymentInSatoshis; // value of LiteCoin Transaction
  private String merchantWalletID;
  private String transactionID;
  private String[] addresses;
  private int expiresInDays;
  private int licenseDaysRemaining;
  private LocalDate licenseGrantedDate;
  private long deltaTime;

  public LitecoinTransactionObject.Output[] outputs;

  /**
   * Create a LiteCoin Transaction Object. This object will provide access to transaction
   * parameters, which will be used to validate the license. The lto provides the transaction value
   * in Satoshis, which are converted to Bitcoin by dividing by 1e8. a transaction:
   * https://api.blockcypher.com/v1/ltc/main/txs/9e9c462b755defda988e9950b9797cecd055a017d32d9c6feff89ebdea5fe3cd
   * my wallet: LMGaFd8tmxJSDbo3GLZFJaPd1kNJ9r1v48
   */
  public TransactionTest() {
    try {
      URL url =
          new URL(
              "https://api.blockcypher.com/v1/ltc/main/txs/5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0");
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
      Gson gson = new GsonBuilder().create();
      LitecoinTransactionObject lto = gson.fromJson(reader, LitecoinTransactionObject.class);

      this.merchantWalletID = "LeUzJqsV3hen3aTrjJWWupFfHcQfrvvyrw";
      this.numberOfConfirmations = lto.getConfirmations();
      System.out.println("numconfs: " + this.numberOfConfirmations);

      this.doubleSpend = lto.getDoubleSpend();
      Instant instant = Instant.parse(lto.getTransactionDate());
      LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
      System.out.println("numconfs: " + this.numberOfConfirmations);
      this.transactionDate = ldt.toLocalDate();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
      System.out.println("formatter: " + formatter);
      //      String formattedString = this.transactionDate.format(formatter);

      // System.out.println("formattedString: " + formattedString);
      this.deltaTime = Duration.between(instant, Instant.now()).toHours();
      System.out.println("delta: " + this.deltaTime);

      // this.transactionDate = Instant.parse(lto.getTransactionDate());

      this.transactionID = "5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0";
      this.licenseDaysRemaining =
          this.expiresInDays - Period.between(this.transactionDate, LocalDate.now()).getDays();
      System.out.println("expires in days: " + this.expiresInDays);
      Period.between(this.transactionDate, LocalDate.now());
      System.out.println(
          "period.between: " + Period.between(this.transactionDate, LocalDate.now()).getDays());

      outputs = lto.getOutputs();
      //   System.out.println("output addresses: " + addresses[0]);
      for (int i = 0; i <= outputs.length - 1; i++) {
        System.out.println("output length: " + outputs.length);
        System.out.println("outputs[]" + outputs[i]);

        try {
          addresses = outputs[i].getAddresses();
          System.out.println("addresses length: " + addresses.length);
          for (int j = 0; j <= addresses.length - 1; j++) {
            try {
              System.out.println("address in try" + addresses[j]);
              if (this.merchantWalletID.equals(addresses[j])) {
                this.paymentInSatoshis = outputs[i].getValue();
                System.out.println("paymentInSatoshis" + this.paymentInSatoshis);
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
    return this.paymentInSatoshis;
  }

  public static void main(String[] args) {
    new TransactionTest();
  }
}
