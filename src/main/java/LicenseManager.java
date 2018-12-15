package bllm;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

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
  private LocalDate trialStartDate;
  private int trialExpiresInDays;
  private int trialRemainingDays;
  private boolean trialExpired;
  

   public double btcPriceInDollars;
   public double ltcPriceInDollars;
 
  // cost refers to what the merchant is requesting
  private String unitsOfCost;  //units of the request; could be dollars that need conversion
  private double cost;  //the requested amount in units specified by unitsOfCost
  //once specified, convert to all currencies
  private double costDollars;
  private double costLTC;
  private double costBTC;
  private double costSatoshis;

  //payment refers to the preferred crypto payment requested
  //determined by the type of wallet ids
  //this is what should appear in the requesting dialog
  private double requestedPayment;
  private String unitsOfRequestedPayment;

  //submitted refers to what, if anything, was submitted
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double btcSubmitted;
  private double satoshisSubmitted;
  
  private String merchantWalletID;


  //variables used when licensed = true
  private LocalDate transactionDate;
  private boolean licensed;
  private LocalDate licenseGrantedDate;
  private String licenseID;
  private String transactionID;
  private int licenseExpiresInDays;
  private int licenseRemainingDays;
  private boolean licenseExpired;
  private int transactionExpiresInHours;
  private boolean walletIDnotFound;
  private  License lic;
  private DialogLicenseManager parent;


  private int licenseStatus;
  private static final int UNLICENSED = 1;
  private static final int TRIAL = 2;
  private static final int LICENSED = 3;
  private static final int TRANSACTION_FAILED = 4;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private String privateKey;
  private LicenseReaderWriter licReadWrite;
  
  public LicenseManager( DialogLicenseManager _parent, String _licenseFileName, String _privateKey ) {
    this.parent = _parent;
    this.privateKey = _privateKey;
    String licenseFileName = _licenseFileName;

      if( new java.io.File(licenseFileName).exists()){
	licReadWrite = new LicenseReaderWriter(parent, licenseFileName, privateKey);
	lic = licReadWrite.readLicense();

	  this.cost = lic.getCost();
	  this.licenseID = lic.getLicenseID();
	  this.unitsOfCost = lic.getUnitsOfCost();
	  this.merchantWalletID = lic.getMerchantWalletID();
	  this.requiredConfirmations= lic.getRequiredConfirmations();
	  this.transactionExpiresInHours = lic.getTransactionExpiresInHours();
	  this.licenseExpiresInDays = lic.getLicenseExpiresInDays();
	  this.trialExpiresInDays = lic.getTrialExpiresInDays();
          this.licenseGrantedDate = lic.getLicenseGrantedDate();
	  this.unitsOfRequestedPayment = lic.getUnitsOfRequestedPayment();

	  //If trial date has not been set then this is the first invocation of the module
	  //immediately write out a new license with today's date as the trial start date
	  if( null == lic.getTrialStartDate()){
	    this.trialStartDate = LocalDate.now();	    
	    lic.setTrialStartDate(LocalDate.now());
	    //trial license is written here
	    licReaderWriter.writeLicense( lic );
	    LOGGER.info("Trial license written with trial start date " + this.trialStartDate);
	  }else{
	    this.trialStartDate = lic.getTrialStartDate();   
	  }

	  //determine how many trial days remain
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
	
	  //determine how many license days remain	  
	  try{ int elapsedLicenseDays = Period.between(this.licenseGrantedDate, LocalDate.now()).getDays();
	    this.licenseRemainingDays = this.licenseExpiresInDays - elapsedLicenseDays;
	    LOGGER.info("expires in days: " + Integer.toString(this.licenseExpiresInDays) );
	    LOGGER.info("elapsed license days: " + Integer.toString(elapsedLicenseDays) );
	    LOGGER.info("remaining license days: " + Integer.toString(this.licenseRemainingDays) );
	    
	    if( this.licenseRemainingDays > 0){
		this.licenseExpired=false;}else{
	      //this.licenseRemainingDays = 0;
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
	    this.satoshisSubmitted = lic.getSatoshisSubmitted();	   
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
	  LOGGER.info("IOException is caught in LicenseManager post license validation");
	} catch (ClassNotFoundException ex) {
	  LOGGER.info("ClassNotFoundException is caught in LicenseManager post license validation");
	} catch (IllegalBlockSizeException ex) {

	} catch (BadPaddingException ex) {

	}catch (NoSuchAlgorithmException ex) {

    } catch (InvalidKeyException ex) {

    } catch (NoSuchPaddingException ex) {

    }
      }


  }

  /**     
   *cost calculations require an internet connection.
   * run only if needed  
   */ 
  public void runCostCalculations(){

    CryptoCalculator cryptoCalculator = new CryptoCalculator();
    this.btcPriceInDollars = cryptoCalculator.getBitcoinPriceInDollars();
    this.ltcPriceInDollars = cryptoCalculator.getLitecoinPriceInDollars();

    switch(unitsOfCost){

    case "Dollars":
      this.costDollars = this.cost;
      this.costBTC = (this.costDollars/this.btcPriceInDollars);
      this.costLTC = (this.costDollars/this.ltcPriceInDollars);
      this.costSatoshis = (this.costDollars/this.btcPriceInDollars)*100000000;
      
      switch(unitsOfRequestedPayment){
      case "Bitcoin":
	this.requestedPayment = (this.costDollars/this.btcPriceInDollars);
	break;
      case "Litecoin":
	this.requestedPayment = (this.costDollars/this.ltcPriceInDollars);
	break;
      }
      break;
    case "Litecoin":
      this.requestedPayment = this.cost;
      this.costLTC = this.cost;
      this.costDollars = this.costLTC*this.ltcPriceInDollars;
      this.costBTC = this.costDollars/this.btcPriceInDollars;
      this.costSatoshis = this.costBTC*100000000;
      break;
    case "Bitcoin":
      this.requestedPayment = this.cost;
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

    switch(unitsOfRequestedPayment){
    case "Litecoin":
    this.dollarSubmitted = (this.satoshisSubmitted/100000000)*this.btcPriceInDollars;
    this.ltcSubmitted = this.dollarSubmitted/this.ltcPriceInDollars;
    break;
    case "Bitcoin":
      this.dollarSubmitted = (this.satoshisSubmitted/100000000)*this.btcPriceInDollars;
    this.btcSubmitted = this.dollarSubmitted/this.btcPriceInDollars ;
    break;
    }
    
    if(this.actualConfirmations >= this.requiredConfirmations &&
       this.doubleSpend == false &&
       this.satoshisSubmitted >= this.costSatoshis &&
       this.deltaTime <= this.transactionExpiresInHours
       ){
      this.licenseGrantedDate = LocalDate.now();
      this.writeOutNewLicense();
      this.licenseStatus = LicenseManager.LICENSED;        
    }else{
      this.licenseStatus = LicenseManager.TRANSACTION_FAILED;  
    }
    LOGGER.info("License status: " + licenseStatus);
    return this.licenseStatus;
  
  }



  public void writeOutNewLicense(){
    
      this.lic.setLicenseGrantedDate( this.licenseGrantedDate );
      this.lic.setTransactionID( this.transactionID);     
      this.lic.setSatoshisSubmitted( this.satoshisSubmitted);
      this.lic.setDollarSubmitted( this.dollarSubmitted);
      
      licReaderWriter.writeLicense(lic);
      parent.displayLicensedPanel();
   
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

  public double getRequestedPayment(){
    return this.requestedPayment;
  }

  public String getUnitsOfRequestedPayment(){
    return this.unitsOfRequestedPayment;
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
 


