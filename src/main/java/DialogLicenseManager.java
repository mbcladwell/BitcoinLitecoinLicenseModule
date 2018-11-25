package bllm;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;


//import java.io.*;
//import java.io.IOException;
//import java.net.*;
import java.util.*;


import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;


import javax.swing.JFrame;


import javax.swing.JComponent;
import javax.swing.SwingConstants;
import java.time.*;
import java.time.format.DateTimeFormatter;

//import java.time.*;
//import java.time.temporal.Temporal;
//import java.time.temporal.ChronoUnit;

import java.nio.charset.Charset;
import javax.swing.border.TitledBorder;


/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
 * 5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0
 */

public class DialogLicenseManager extends JFrame  {

  private static final long serialVersionUID = 1L;

  private long paymentInSatoshis;  //value of LiteCoin Transaction
  private long deltaTime;  // time difference between now and transaction time in hours
  private long requiredConfirmations;
  private long actualConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;
  

   public double btcPriceInDollars;
   public double ltcPriceInDollars;
 
  // Limits to determine license validity
  private String unitsOfCost;  
  private double costLTC;  
  private double costDollars;  
  private double costSatoshis;  
  
  private String merchantWalletID;

  
  //variables used when licensed = true
  //  private boolean licensed;
  private boolean displayTrialBanner;
  private LocalDate licenseGrantedDate;
  private LocalDate trialStartDate;
  private double cost;
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double satoshiSubmitted;
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
  private LicenseManager lm;
  
  public DialogLicenseManager( String _licenseFileName ) {
    this.licenseFileName = _licenseFileName;
    lm = new LicenseManager( this, licenseFileName);
    licenseStatus = lm.getLicenseStatus();
    this.unitsOfCost = lm.getUnitsOfCost();

	  switch(licenseStatus){
	    
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
	  

	}
    
  

  
  public void displayLicensedPanel(){
	    pane3 = new LicensedPanel(this,
				      lm.getMerchantWalletID(),
				      lm.getLicenseID(),
				      lm.getTransactionID(),
				      lm.getLicenseGrantedDate(),
				      lm.getLicenseRemainingDays(),
				      lm.getPayment(),  
				      lm.getUnitsOfPayment(),
				      lm.getDollarSubmitted()); 
  }
  
  public void displayUnlicensedPanel(){
	      pane1 = new UnlicensedPanel(this,
					  this.displayTrialBanner,
					  lm.getTrialRemainingDays(),
					  lm.getCost(),
					      lm.getUnitsOfCost(),
					  lm.getMerchantWalletID(),
					  lm.getLicenseID(),
					  lm.getRequiredConfirmations(),
					  lm.getTransactionExpiresInHours(),
					  this.lm);
	      
    
  }
  public void displayTransactionFailedPanel(){
    pane2 = new TransactionFailedPanel(this,
				       lm.getCostDollars(),
				       lm.getCost(),
				       lm.getUnitsOfCost(),
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
				       lm.getWalletIDnotFound());
  }
	

  }





  



  

 


