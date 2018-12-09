package bllm;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;


import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.util.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;

import java.nio.charset.Charset;
import javax.swing.border.TitledBorder;
import java.util.logging.*;


/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
 * 5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0
 * ec9f1f272d76b028ee54b6f650dd2c5f2efa2c74335dfbb07584a511fb0bbc04    bitcoin 0.06
 */

public class LicenseManager   {


  //private long paymentInSatoshis;  
  private long deltaTime;  // time difference between now and transaction time in hours
  private long requiredConfirmations;
  private long actualConfirmations;
  private boolean doubleSpend;
  private LocalDate transactionDate;
  

   public double btcPriceInDollars;
   public double ltcPriceInDollars;
 
  // Limits to determine license validity
  private String unitsOfCost;  
  private double cost;  //the requested amount in (undetermined) units
  private double costDollars;
  private double costLTC;
  private double costBTC;
  private double costSatoshis;  
  private double payment;
  private String unitsOfPayment;
  
  private String merchantWalletID;

  
  //variables used when licensed = true
  private boolean licensed;
  private LocalDate licenseGrantedDate;
  private LocalDate trialStartDate;
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double btcSubmitted;
  private double satoshisSubmitted;
  private String licenseID;
  private String transactionID;
  private int licenseExpiresInDays;
  private int licenseRemainingDays;
  private boolean licenseExpired;
  private int transactionExpiresInHours;
  private int trialExpiresInDays;
  private int trialRemainingDays;
  private boolean trialExpired;
  private boolean walletIDnotFound;
  private  License lic;
  private DialogLicenseManager parent;


  private int licenseStatus;
  private static final int UNLICENSED = 1;
  private static final int TRIAL = 2;
  private static final int LICENSED = 3;
  private static final int TRANSACTION_FAILED = 4;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  
  public LicenseManager( DialogLicenseManager _parent, String _licenseFileName ) {
    this.parent = _parent;
    
       String licenseFileName = _licenseFileName;
      if( new File(licenseFileName).exists()){

	try {
	  FileInputStream file = new FileInputStream(licenseFileName);
	  ObjectInputStream in = new ObjectInputStream(file);
	  LOGGER.info("License read:  " + licenseFileName);
	 
	  this.lic = (License) in.readObject();

	  in.close();

	  this.cost = lic.getCost();
	  this.licenseID = lic.getLicenseID();
	  this.unitsOfCost = lic.getUnitsOfCost();
	  this.merchantWalletID = lic.getMerchantWalletID();
	  this.requiredConfirmations= lic.getRequiredConfirmations();
	  this.transactionExpiresInHours = lic.getTransactionExpiresInHours();
	  this.licenseExpiresInDays = lic.getLicenseExpiresInDays();
	  this.trialExpiresInDays = lic.getTrialExpiresInDays();
          this.licenseGrantedDate = lic.getLicenseGrantedDate();
	  this.unitsOfPayment = lic.getUnitsOfPayment();

	  if( null == lic.getTrialStartDate()){
	    lic.setTrialStartDate(LocalDate.now());
	    this.writeOutTrialLicense();
	    this.trialStartDate = LocalDate.now();
	  }else{
	    this.trialStartDate = lic.getTrialStartDate();   
	  }

	  try{ int elapsedTrialDays = Period.between(this.trialStartDate, LocalDate.now()).getDays();
	    this.trialRemainingDays = this.trialExpiresInDays - elapsedTrialDays;
	    if(this.trialRemainingDays > 0){
	      this.trialExpired=false;}else{
	      this.trialRemainingDays = 0;
	      this.trialExpired=true;
	    }
	    
	  }catch(Exception e){
	      this.trialRemainingDays = 0;
	    this.trialExpired=true;
	  }
	
		  
	  try{ int elapsedLicenseDays = Period.between(this.licenseGrantedDate, LocalDate.now()).getDays();
	    this.licenseRemainingDays = this.licenseExpiresInDays - elapsedLicenseDays;
	    LOGGER.info("expires in days: " + Integer.toString(this.licenseExpiresInDays) );
	    LOGGER.info("elapsed license days: " + Integer.toString(elapsedLicenseDays) );
	    LOGGER.info("remaining license days: " + Integer.toString(this.licenseRemainingDays) );
	    
	    if( this.licenseRemainingDays > 0){
		this.licenseExpired=false;}else{
		this.licenseRemainingDays = 0;
		this.licenseExpired=true;
	      }
	  }catch(Exception e){
	    this.licenseRemainingDays = 0;
	    this.licenseExpired=true;
	  }

	  if (!this.licenseExpired) {
	    this.licenseStatus = LicenseManager.LICENSED;
	  }else{if(!trialExpired){
	      this.licenseStatus = LicenseManager.TRIAL;
	    }else{
	      this.licenseStatus = LicenseManager.UNLICENSED;
	    }
	  }


	  switch(licenseStatus){
	    
	  case LicenseManager.LICENSED:
	    this.satoshisSubmitted = lic.getAmountPaid();	   
	    this.transactionID = lic.getTransactionID();
	    this.dollarSubmitted = lic.getDollarSubmitted();
	    
	    break;
	    
	  case LicenseManager.TRIAL:
	    this.runCostCalculations();
	    break;
	
	  case LicenseManager.UNLICENSED:
	    this.runCostCalculations();	
	    break;
	    
	  }
	} catch (IOException ex) {
	  System.out.println("IOException is caught");
	} catch (ClassNotFoundException ex) {
	  System.out.println("ClassNotFoundException is caught");
	}
      }


  }

  //cost calculations require an internet connection.
  //run only if needed
  public void runCostCalculations(){

    CryptoCalculator cryptoCalculator = new CryptoCalculator();
    this.btcPriceInDollars = cryptoCalculator.getBitcoinPriceInDollars();
    this.ltcPriceInDollars = cryptoCalculator.getLitecoinPriceInDollars();

    switch(unitsOfCost){

    case "Dollars":
      this.costDollars = this.cost;
      this.costBTC = (this.costDollars/this.btcPriceInDollars);
      this.costLTC = (this.costDollars/this.ltcPriceInDollars);
      switch(unitsOfPayment){
      case "Bitcoin":
	this.costSatoshis = (this.costDollars/this.btcPriceInDollars)*100000000;
	break;
      case "Litecoin":
	this.costSatoshis = (this.costDollars/this.ltcPriceInDollars)*100000000;
	break;
      }
      break;
    case "Litecoin":
      this.costLTC = this.cost;
      this.costDollars = this.costLTC*this.ltcPriceInDollars;
      this.costBTC = this.costDollars/this.btcPriceInDollars;
      this.costSatoshis = (this.costDollars/this.ltcPriceInDollars)*100000000;
      break;
    case "Bitcoin":
      this.costBTC = this.cost;
      this.costSatoshis = this.costBTC*100000000;
      this.costDollars = this.costBTC*this.btcPriceInDollars;
      this.costLTC = (this.costDollars/this.ltcPriceInDollars);
      break;
    }
    
  }

  
  public int evaluateTransaction( String transactionID){

    Transaction transaction = new Transaction( transactionID, this.merchantWalletID, unitsOfCost);
    this.transactionID = transactionID;
    this.actualConfirmations = transaction.getNumberOfConfirmations();
    this.doubleSpend = transaction.getDoubleSpend();
    this.transactionDate =  transaction.getTransactionDate();
    this.satoshisSubmitted = transaction.getPaymentInSatoshis();
    this.deltaTime = transaction.getDeltaTime();
    this.walletIDnotFound = transaction.getWalletIDnotFound();

    switch(unitsOfPayment){
    case "Litecoin":
    this.dollarSubmitted = (this.satoshisSubmitted/100000000)*this.ltcPriceInDollars;
    this.ltcSubmitted = this.dollarSubmitted/this.ltcPriceInDollars;
    this.payment = this.ltcSubmitted;
    this.unitsOfPayment = "Litecoin";
    break;
    case "Bitcoin":
      this.dollarSubmitted = (this.satoshisSubmitted/100000000)*this.btcPriceInDollars;
    this.btcSubmitted = this.dollarSubmitted/this.btcPriceInDollars ;
    this.payment = this.btcSubmitted;
    this.unitsOfPayment = "Bitcoin";
    break;
    }
    
    if(this.actualConfirmations >= this.requiredConfirmations &&
       this.doubleSpend == false &&
       this.satoshisSubmitted >= this.costSatoshis &&
       this.deltaTime <= this.transactionExpiresInHours
       ){
      this.licenseGrantedDate = LocalDate.now();
      lic.setLicenseGrantedDate( LocalDate.now() );
      this.writeOutNewLicense();
      this.licenseStatus = LicenseManager.LICENSED;        
    }else{
      this.licenseStatus = LicenseManager.TRANSACTION_FAILED;  
    }
    System.out.println("license status: " + licenseStatus);
    return this.licenseStatus;
  
  }



  public void writeOutNewLicense(){
    try {
      //      this.lic.setLicensed(true);
      this.lic.setLicenseGrantedDate( LocalDate.now() );
      this.lic.setTransactionID( this.transactionID);
      
      this.lic.setSatoshisSubmitted( this.satoshisSubmitted);
      this.lic.setDollarSubmitted( this.dollarSubmitted);
      
      String filename = new String( "./license.ser");

        // Saving of object in a file
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
      out.writeObject(this.lic);

      out.close();
      file.close();
      System.out.println("An updated license file has been serialized to: " + filename);
      parent.displayLicensedPanel();
    }
     catch (IOException ex) {
      System.out.println("IOException is caught");
    }
  }

  public void writeOutTrialLicense(){
    try {
      
      String filename = new String( "./license.ser");

        // Saving of object in a file
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
      out.writeObject(this.lic);

      out.close();
      file.close();
      LOGGER.info("License written with trial period start date: " + lic.getTrialStartDate());
    }
     catch (IOException ex) {
      System.out.println("IOException is caught");
    }
  }

  public int getLicenseStatus(){
    return this.licenseStatus;
  }
  public double getCost(){
    return this.cost;
  }
  public String getUnitsOfCost(){
    return this.unitsOfCost;
  }
  public double getCostDollars(){
    return this.costDollars;
  }

  public double getDollarSubmitted(){
    return this.dollarSubmitted;
  }

  public boolean getDoubleSpend(){
    return this.doubleSpend;
  }

  public long getDeltaTime(){
    return this.deltaTime;
  }

  public long getActualConfirmations(){
    return this.actualConfirmations;
  }

  public double getLTCSubmitted(){
    return this.ltcSubmitted;
  }

  public double geBTCSubmitted(){
    return this.btcSubmitted;
  }

  public double getPayment(){
    return this.payment;
  }

  public String getUnitsOfPayment(){
    return this.unitsOfPayment;
  }

  
  public int getTransactionExpiresInHours(){
    return this.transactionExpiresInHours;
  }
  
  public boolean getWalletIDnotFound(){
    return this.walletIDnotFound;
  }
  public int getTrialExpiresInDays(){
    return this.trialExpiresInDays;
  }
  public int getTrialRemainingDays(){
    return this.trialRemainingDays;
  }
  public int getLicenseRemainingDays(){
    return this.licenseRemainingDays;
  }
  public String getMerchantWalletID(){
    return this.merchantWalletID;
  }
  public String getLicenseID(){
    return this.licenseID;
  }

  public long getRequiredConfirmations(){
    return this.requiredConfirmations;
  }
  public String getTransactionID(){
    return this.transactionID;
  }
  public LocalDate getLicenseGrantedDate(){
    return this.licenseGrantedDate;
  }
}
 


