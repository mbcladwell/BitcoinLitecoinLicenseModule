package llm;

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


/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
 * 5dc22fd37b251b871803481fa33ee671b7cc429520428d3fce82451596926ff0
 */

public class LicenseManager   {


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
  private boolean licensed;
  private LocalDate licenseGrantedDate;
  private LocalDate trialStartDate;
  private double cost;
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double satoshiSubmitted;
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
  private  llm.License lic;
  private DialogLicenseManager parent;


  private int licenseStatus;
  private static final int UNLICENSED = 1;
  private static final int TRIAL = 2;
  private static final int LICENSED = 3;
  private static final int TRANSACTION_FAILED = 4;
  
  
  public LicenseManager( DialogLicenseManager _parent, String _licenseFileName ) {
    this.parent = _parent;
    
    //String filename = "/home/mbc/syncd/prog/llm/build/classes/java/main/license.ser";
       String licenseFileName = _licenseFileName;
      if( new File(licenseFileName).exists()){

	try {
	  FileInputStream file = new FileInputStream(licenseFileName);
	  ObjectInputStream in = new ObjectInputStream(file);
	  System.out.println("License read:  " + licenseFileName);
	 
	  this.lic = (llm.License) in.readObject();

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
	    this.satoshiSubmitted = lic.getAmountPaid();	   
	    this.transactionID = lic.getTransactionID();
	    this.ltcSubmitted = lic.getLTCSubmitted();
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

  
  public void runCostCalculations(){

    CryptoCalculator cryptoCalculator = new CryptoCalculator();
    this.btcPriceInDollars = cryptoCalculator.getBitcoinPriceInDollars();
    this.ltcPriceInDollars = cryptoCalculator.getLitecoinPriceInDollars();

    switch(unitsOfCost){

    case "Dollars":
      this.costDollars = this.cost;
      this.costLTC = this.costDollars/this.ltcPriceInDollars;
      this.costSatoshis = (this.costDollars/this.btcPriceInDollars)*100000000;
      break;
    case "Litecoin":
      this.costLTC = this.cost;
      this.costDollars = this.costLTC*this.ltcPriceInDollars;
      this.costSatoshis = (this.costDollars/this.btcPriceInDollars)*100000000;
      break;

      
    }
    
  }

  
  public int evaluateTransaction( String transactionID){

    Transaction transaction = new Transaction( transactionID, this.merchantWalletID);
    this.transactionID = transactionID;
    this.actualConfirmations = transaction.getNumberOfConfirmations();
    this.doubleSpend = transaction.getDoubleSpend();
    this.transactionDate =  transaction.getTransactionDate();
    this.paymentInSatoshis = transaction.getPaymentInSatoshis();
    this.deltaTime = transaction.getDeltaTime();
    this.walletIDnotFound = transaction.getWalletIDnotFound();
      
  
    this.dollarSubmitted = (this.paymentInSatoshis/10000000)*this.btcPriceInDollars;
    this.ltcSubmitted = this.dollarSubmitted/this.ltcPriceInDollars ;
    
    if(this.actualConfirmations >= this.requiredConfirmations &&
       this.doubleSpend == false &&
       this.paymentInSatoshis >= this.costSatoshis &&
       this.deltaTime <= this.transactionExpiresInHours
       ){
      this.licenseGrantedDate = LocalDate.now();
      lic.setLicenseGrantedDate( LocalDate.now() );
      this.writeOutNewLicense();
	    this.licenseStatus = LicenseManager.LICENSED;
      
      
    }else{
      this.licenseStatus = LicenseManager.TRANSACTION_FAILED;  
    }
    return this.licenseStatus;
  
  }

  
 

  public void writeOutNewLicense(){
    try {
      //      this.lic.setLicensed(true);
      this.lic.setLicenseGrantedDate( LocalDate.now() );
      this.lic.setTransactionID( this.transactionID);
      this.lic.setLTCSubmitted( this.ltcSubmitted);
      this.lic.setDollarSubmitted( this.dollarSubmitted);
      
      String filename = new String( "./license.ser");

        // Saving of object in a file
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
      out.writeObject(this.lic);

      out.close();
      file.close();
      System.out.println("An updated license file has been serialized to disk.");
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
      System.out.println("License written with trial period start date.");
    }
     catch (IOException ex) {
      System.out.println("IOException is caught");
    }
  }

  public int getLicenseStatus(){
    return this.licenseStatus;
  }
  public double getCostLTC(){
    return this.costLTC;
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
 


