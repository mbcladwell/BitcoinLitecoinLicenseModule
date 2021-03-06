package bllm;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
 * 5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0
 */

public class DialogLicenseManager extends JFrame {

  private static final long serialVersionUID = 1L;

  private long deltaTime; // time difference between now and transaction time in hours
  private long requiredConfirmations;
  private long actualConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;

  public double btcPriceInDollars;
  public double ltcPriceInDollars;

  // unitsOfCost and cost provided by merchant in bllmlkg
  private String unitsOfCost;
  private double cost;

  // determine cost in each of the cryptocurrencies
  private double costLTC;
  private double costDollars;
  private double costSatoshis;

  // determine payment in each of the cryptocurrencies
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double satoshiSubmitted;
  // private long paymentInSatoshis;

  private String merchantWalletID;

  // variables used when licensed = true
  //  private boolean licensed;
  private boolean displayTrialBanner;
  private LocalDate licenseGrantedDate;
  private LocalDate trialStartDate;
  private String licenseID;
  private String transactionID;
  private int licenseExpiresInDays;
  private boolean licenseExpired;
  private int transactionExpiresInHours;
  private int trialExpiresInDays;
  private boolean trialExpired;
  private boolean walletIDnotFound;
  private License lic;

  private JPanel cards; // a panel that uses CardLayout
  private CardLayout cl = new CardLayout();
  private JPanel parentPane;
  private UnlicensedPanel pane1;
  private TransactionFailedPanel pane2;
  private LicensedPanel pane3;

  private int licenseStatus;
  private static final int UNLICENSED = 1;
  private static final int TRIAL = 2;
  private static final int LICENSED = 3;
  private static final int TRANSACTION_FAILED = 4;
  private String licenseFileName;
  private String privateKey;
  private LicenseManager lm;
  private String title;

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public DialogLicenseManager(String _title, String _licenseFileName, String _privateKey) {
    LOGGER.setLevel(Level.INFO);
    this.title = _title;
    this.licenseFileName = _licenseFileName;
    this.privateKey = _privateKey;
    lm = new LicenseManager(this, licenseFileName, privateKey);
    licenseStatus = lm.getLicenseStatus();
    this.unitsOfCost = lm.getUnitsOfCost();

    switch (licenseStatus) {
      case DialogLicenseManager.LICENSED:
        this.displayLicensedPanel();
        break;

      case DialogLicenseManager.TRIAL:
        this.displayTrialBanner = true;
        this.displayUnlicensedPanel();
        break;

      case DialogLicenseManager.UNLICENSED:
        this.displayTrialBanner = false;
        this.displayUnlicensedPanel();

        break;

      case DialogLicenseManager.TRANSACTION_FAILED:
        this.displayTransactionFailedPanel();
        break;
    }
    this.setVisible(true);
  }

  public void displayLicensedPanel() {
    LOGGER.info("licenseRemainingDay: " + lm.getLicenseRemainingDays());
    pane3 =
        new LicensedPanel(
            this,
            lm.getMerchantWalletID(),
            lm.getLicenseID(),
            lm.getTransactionID(),
            lm.getLicenseGrantedDate(),
            lm.getLicenseRemainingDays(),
            lm.getActualPayment(),
            lm.getUnitsOfRequestedPayment(),
            lm.getDollarSubmitted(),
            this.title);
  }

  public void displayUnlicensedPanel() {
    pane1 =
        new UnlicensedPanel(
            this,
            this.displayTrialBanner,
            lm.getTrialRemainingDays(),
            lm.getRequestedPayment(),
            lm.getUnitsOfRequestedPayment(),
            lm.getMerchantWalletID(),
            lm.getLicenseID(),
            lm.getRequiredConfirmations(),
            lm.getTransactionExpiresInHours(),
            this.lm,
            this.title);
  }

  public void displayTransactionFailedPanel() {
    pane2 =
        new TransactionFailedPanel(
            this,
            lm.getCostDollars(),
            lm.getRequestedPayment(),
            lm.getUnitsOfRequestedPayment(),
            lm.getMerchantWalletID(),
            lm.getLicenseID(),
            lm.getTransactionID(),
            lm.getRequiredConfirmations(),
            lm.getTransactionExpiresInHours(),
            lm.getDoubleSpend(),
            lm.getDeltaTime(),
            lm.getActualConfirmations(),
            lm.getDollarSubmitted(),
            lm.getLTCSubmitted(),
            lm.getWalletIDnotFound(),
            this.title);
  }
}
