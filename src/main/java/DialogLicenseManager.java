package llm;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;


import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.util.*;
//import java.util.Date;


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

//import java.time.Instant;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;

import java.nio.charset.Charset;
import javax.swing.border.TitledBorder;


/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
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
  private boolean licensed;
  private LocalDate licenseGrantedDate;
  private double cost;
  private double dollarSubmitted;
  private double ltcSubmitted;
  private double satoshiSubmitted;
  private String licenseID;
  private String transactionID;
  private int expiresInDays;  //license
  private long licenseDaysRemaining;
  private int expiresInHours;  //transaction
  private boolean walletIDnotFound;
  private  llm.License lic;

  private JPanel cards; // a panel that uses CardLayout
  private CardLayout cl = new CardLayout();
  private JPanel parentPane;
  private UnlicensedPanel pane1;
  private TransactionFailedPanel pane2;
  private LicensedPanel pane3;
  
  public DialogLicenseManager(  ) {

    
    //String filename = "/home/mbc/syncd/prog/llm/build/classes/java/main/license.ser";
       String filename = "./license.ser";
      if( new File(filename).exists()){

	try {
	  FileInputStream file = new FileInputStream(filename);
	  ObjectInputStream in = new ObjectInputStream(file);
	  System.out.println("License read:  " + filename);
	 
	  this.lic = (llm.License) in.readObject();

	  in.close();

	  this.cost = lic.getCost();
	  this.licenseID = lic.getLicenseID();
	  this.unitsOfCost = lic.getUnitsOfCost();
	  this.merchantWalletID = lic.getMerchantWalletID();
	  this.requiredConfirmations= lic.getRequiredConfirmations();
	  this.expiresInHours = lic.getExpiresInHours();
	  this.expiresInDays = lic.getExpiresInDays();
	 
	  
	  if (lic.getLicensed()) {
	    this.licensed = true;
	    this.licenseGrantedDate = lic.getLicenseGrantedDate();
	    
	      this.licenseDaysRemaining = this.expiresInDays - Period.between(this.licenseGrantedDate, LocalDate.now()).getDays();
	    this.satoshiSubmitted = lic.getAmountPaid();	   
	    this.transactionID = lic.getTransactionID();
	    this.ltcSubmitted = lic.getLTCSubmitted();
	    this.dollarSubmitted = lic.getDollarSubmitted();
	    
	    
          pane3 = new LicensedPanel(this,
				    this.merchantWalletID,
				    this.licenseID,
				    this.transactionID,
				    this.licenseGrantedDate,
				    this.licenseDaysRemaining,
				    this.ltcSubmitted,
				    this.dollarSubmitted); 
	    
	  }
	  else {
	    this.runCostCalculations();
	    this.displayUnlicensedPanel();
	   
       
	  }

	  
	} catch (IOException ex) {
	  System.out.println("IOException is caught");
	} catch (ClassNotFoundException ex) {
	  System.out.println("ClassNotFoundException is caught");
	}
      } else{
	JOptionPane.showMessageDialog(this,
				      "License file does not exist!",
				      "Error", JOptionPane.ERROR_MESSAGE);
	System.out.println("License file does not exist!");
	dispose();
      }
  



      
    this.parentPane = new JPanel(new BorderLayout());
    this.parentPane.setBorder(BorderFactory.createRaisedBevelBorder());

    cards = new JPanel(this.cl);
    

  }

  public void displayUnlicensedPanel(){
    pane1 = new UnlicensedPanel(this,
				this.costLTC,
				this.merchantWalletID,
				this.licenseID,
				this.requiredConfirmations,
				this.expiresInHours); 
  }

    public void displayTransactionFailedPanel(){
      
      pane2 = new TransactionFailedPanel(this,
					 this.costDollars,
					 this.costLTC,
					 this.merchantWalletID,
					 this.licenseID,
					 this.transactionID,
					 this.requiredConfirmations,
					 this.expiresInHours,
					 this.doubleSpend,
					 this.deltaTime,
					 this.actualConfirmations,
					 this.dollarSubmitted,
					 this.ltcSubmitted,
					 this.walletIDnotFound
					 );
    }

  public void displayLicensedPanel(){
    pane3 = new LicensedPanel(this,
			      this.merchantWalletID,
			      this.licenseID,
			      this.transactionID,
			      this.licenseGrantedDate,
			      this.licenseDaysRemaining,
			      this.ltcSubmitted,
			      this.dollarSubmitted); 
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

  
  public void evaluateTransaction( String transactionID){

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
       this.deltaTime <= this.expiresInHours
       ){
      this.licenseGrantedDate = LocalDate.now();
      this.licenseDaysRemaining = this.expiresInDays - Period.between(this.transactionDate, LocalDate.now()).getDays();
      this.writeOutNewLicense();
      this.displayLicensedPanel();
      
      
    }else{
      this.displayTransactionFailedPanel();
      
      
 }
    
  
  }

  
 
  public String getMerchantWalletID(){
    return this.merchantWalletID;
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
    }
     catch (IOException ex) {
      System.out.println("IOException is caught");
    }
  }
    
}
 


