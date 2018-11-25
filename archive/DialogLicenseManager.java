package llm;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;


import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.*;
//import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

import javax.swing.JComponent;
//import java.awt.Color;
import javax.swing.SwingConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.JTextField;
import java.time.*;
import java.time.format.DateTimeFormatter;

//import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;

import java.nio.charset.Charset;
import javax.swing.border.TitledBorder;


/*
 * Terminology:
 * customer: person purchasing license
 * merchant: person selling license i.e. developer
 */

public class DialogLicenseManager extends JFrame {

  private static final long serialVersionUID = 1L;


  public static final int DOLLARS = 1;
  public static final int LTC = 2;
  public static final int SATOSHIS = 3;
  
  
  JPanel cards; // a panel that uses CardLayout
  CardLayout cl = new CardLayout();
  public static final String INTROPANEL = "Card showing data entry box";
  public static final String UNLICENSEDPANEL = "Card showing unlicensed view";
  public static final String LICENSEDPANEL = "Card showing licensed view";

  static JButton button;
  static JLabel licenseKey;
  static JLabel picLabel;
  static JLabel label;
  static JTextField textField;
  
  static JButton okButton;
  static JButton cancelButton;

  
  private boolean transactionValid;
  private LocalDateTime received;  
  private JLabel  doubleSpendLabel;
  private JLabel  elapsedTimeLabel;
  public JLabel  confirmationsLabel;
  private JLabel  dollarValueLabel;
  private JLabel  ltcValueLabel;

 
  // from lto class
  private double paymentInDollars; // value of Litecoin Transaction in Dollars
  private double paymentInBitcoin;
  private long paymentInSatoshis;  //value of LiteCoin Transaction
  private long deltaTime;  // time difference between now and transaction time in seconds;  8 hours is 28800
  private long numberOfConfirmations;
  private boolean doubleSpend;
  private long transactionDate;
  

   public double btcPriceInDollars;
   public double ltcPriceInDollars;
 
  // Limits to determine license validity
  private String unitsOfCost;  
  private double costLTC;  
  private double costDollars;  
  private double costSatoshis;  
  
  private String merchantWalletID;
  private long requiredConfirmations;

  
  //variables used when isLicensed = true
  private boolean isLicensed;
   private Instant licenseGrantedDate;
  private double cost;
  private double amountPaid;
  private String licenseID;
  private String transactionID;
  private int expiresInDays;  //license
  private int expiresInHours;  //transaction
  
  public DialogLicenseManager( ) {

    this.setTitle("Litecoin License Module  " + LocalDate.now() );
    this.setResizable(true);

    Image img =
      new ImageIcon(DialogLicenseManager.class.getResource("../../../resources/main/ltc.png")).getImage();
       this.setIconImage(img);
       //read in the license and set parameters
       
       String filename = "/home/mbc/syncd/prog/llm/build/classes/java/main/license.ser";
       //String filename = "../license.ser";
       //String filename = "../../../resources/main/license.ser";
       //URL url = getClass().getResource("/home/mbc/syncd/prog/llm/build/classes/java/main/license.ser");
      if( new File(filename).exists()){

	CryptoCalculator cryptoCalculator = new CryptoCalculator();
	this.btcPriceInDollars = cryptoCalculator.getBitcoinPriceInDollars();
	this.ltcPriceInDollars = cryptoCalculator.getLitecoinPriceInDollars();

	try {
	 
     
       	 FileInputStream file = new FileInputStream(filename);
	 ObjectInputStream in = new ObjectInputStream(file);
      System.out.println("License read pre:  " + filename);
	 
	 // Method for serialization of object
	 llm.License lic = (llm.License) in.readObject();
      System.out.println("License read");

      in.close();
      //file.close();

      System.out.println("License has been de-serialized");
      System.out.println("Cost: " + String.valueOf(lic.getCost()));
      System.out.println("License ID: " + lic.getLicenseID());
      System.out.println("Expires in days: " + String.valueOf(lic.getExpiresInDays()));
      System.out.println("ExpiresInHours: " + String.valueOf(lic.getExpiresInHours()));
      System.out.println("UnitsOfCost: " + String.valueOf(lic.getUnitsOfCost()));
      System.out.println("RequiredConfirmations: " + String.valueOf(lic.getRequiredConfirmations()));
      System.out.println("MerchantLTCaddress: " + String.valueOf(lic.getMerchantWalletID()));
     
      this.cost = lic.getCost();
      this.licenseID = lic.getLicenseID();
      this.unitsOfCost = lic.getUnitsOfCost();
      this.merchantWalletID = lic.getMerchantWalletID();
      this.requiredConfirmations= lic.getRequiredConfirmations();
      this.expiresInHours = lic.getExpiresInHours();
      this.expiresInDays = lic.getExpiresInDays();
	
	  if (lic.getLicensed()) {
	    this.isLicensed = true;
	    this.licenseGrantedDate = lic.getLicenseGrantedDate();	    
	    this.amountPaid = lic.getAmountPaid();	   
	    this.transactionID = lic.getTransactionID();
	    
	  }
	  else {
	    
	  }

      System.out.println("this.cost " + this.cost);
	  
    } catch (IOException ex) {
      System.out.println("IOException is caught");
    } catch (ClassNotFoundException ex) {
      System.out.println("ClassNotFoundException is caught");
       }
       }
       else{System.out.println("file does not exist!");}

  

       
    
    //Set up the top "NORTH" panel which will contain the license status
    //this pane will be reused on all 3 cards
    //Make a separate JPanel that can use etched borders
    JPanel licenseLabelPane = new JPanel(new BorderLayout());
    licenseLabelPane.setBorder(BorderFactory.createTitledBorder(""));

    JLabel licenseLabel = new JLabel("Unlicensed", JLabel.CENTER);
    licenseLabel.setForeground( Color.red);
    licenseLabel.setFont(new Font("Serif", Font.BOLD, 25));
    licenseLabelPane.add(licenseLabel, BorderLayout.CENTER);


    
    ////////////////////////////////////////////////////////////////
    //Set up Pane 1     Unlicensed

    JPanel parentPane = new JPanel(new BorderLayout());
    parentPane.setBorder(BorderFactory.createRaisedBevelBorder());



    JPanel pane1 = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
 
    
    label = new JLabel("Follow instructions below to obtain license. Internet connection required.");
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(5,5,5,5);  
    c.weightx = 1;
    c.weighty = 1;
    pane1.add(label, c);

    label = new JLabel("1.");
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(label, c);

    label = new JLabel("Transfer " +  this.cost +    " Litecoin to wallet ID: ");
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(label, c);

   label = new JLabel(this.merchantWalletID);
    c.gridx = 2;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(label, c);

    
    JButton clipboardButton = new JButton();
    // clipboardButton.setMnemonic(KeyEvent.VK_Y);
    clipboardButton.setToolTipText("Copy wallet ID to clipboard");
    
    try {
      // JLabel ltclabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("../../resources/main/ltc.png")));
  
	Image clipImage = ImageIO.read(getClass().getClassLoader().getResource("../../resources/main/clipboard2.png"));
   
	ImageIcon ii = new ImageIcon(clipImage.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ));
	clipboardButton.setIcon(ii);
    } catch (Exception ex) {
      System.out.println(ex);
    } 
    
    c.gridx = 4;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    clipboardButton.addActionListener(
        (new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      StringSelection stringSelection = new StringSelection( ( ((DialogLicenseManager)((JButton)e.getSource()).getTopLevelAncestor())).getMerchantWalletID() );
	      
	      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	      clpbrd.setContents( stringSelection, null);
	    }
	  }));
    pane1.add(clipboardButton, c);
    
    label = new JLabel("2.");
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane1.add(label, c);

    label = new JLabel("Wait for " + this.requiredConfirmations + " confirmations.");
    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane1.add(label, c);

    label = new JLabel("3.");
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    pane1.add(label, c);

    label = new JLabel("Enter transaction ID within " + this.expiresInHours  + " hours:");
    c.gridx = 1;
    c.gridy = 3;
    c.gridwidth = 1;
    pane1.add(label, c);

    
    textField = new JTextField(40);
    c.gridx = 2;
    c.gridy = 3;
    c.gridwidth = 4;
    c.fill = GridBagConstraints.HORIZONTAL;
 
    pane1.add(textField, c);

    label = new JLabel("4.");
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    pane1.add(label, c);

 okButton = new JButton("Retrieve transaction");
    okButton.setMnemonic(KeyEvent.VK_R);
    okButton.setActionCommand("ok");
    okButton.setEnabled(true);
    okButton.putClientProperty( "wallet", this.merchantWalletID );
    okButton.setToolTipText("Extract transaction details from blockcypher.com");
    //c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    //https://stackoverflow.com/questions/11037622/pass-variables-to-actionlistener-in-java
            Transaction transaction = new Transaction( textField.getText(), (String)((JButton)e.getSource()).getClientProperty( "wallet" ));
          }
        }));

    pane1.add(okButton, c);


    label = new JLabel("to confirm payment and obtain license.");
    c.gridx = 2;
    c.gridy = 4;
    c.gridwidth = 1;
    pane1.add(label, c);
    
    cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("cancel");
    cancelButton.setEnabled(true);
    //cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx = 4;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    pane1.add(cancelButton, c);

    /////////////////////////////////////////////////////////
    //Configure pane2   Unlicensed - failed transaction

    JPanel pane2 = new JPanel(new GridBagLayout());
    pane2.setBorder(BorderFactory.createRaisedBevelBorder()); 
    
   label = new JLabel("Doublespend?:", SwingConstants.RIGHT);
    c.fill = 0;
    c.gridx = 0;
    c.gridy = 2;
    pane2.add(label, c);

    
    doubleSpendLabel = new JLabel( "");
    //if(this.doubleSpend){doubleSpendLabel.setForeground(Color.red);}else{ doubleSpendLabel.setForeground(Color.green.darker());}
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 2;
    pane2.add(doubleSpendLabel, c);

    /*
    JLabel ltclabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("../../resources/main/ltc.png")));
    ltclabel.setPreferredSize(new Dimension(100, 100));
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 3;
    c.gridheight = 7;
    c.weightx = 1;
    c.weighty = 1;
    pane2.add(ltclabel, c);
    */
    
    label = new JLabel("Elapsed time (hours):");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.gridheight = 1;
     c.anchor = GridBagConstraints.LINE_END;
    pane2.add(label, c);
    
    elapsedTimeLabel =new JLabel (""); 
    c.gridx = 1;
    c.gridy = 3;
    pane2.add(elapsedTimeLabel, c);

    
    label = new JLabel("Confirmations:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 4;
    pane2.add(label, c);


     confirmationsLabel =new JLabel (""); 
    c.gridx = 1;
    c.gridy = 4;
    pane2.add(confirmationsLabel, c);

    label = new JLabel("Value:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    pane2.add(label, c);

    dollarValueLabel =new JLabel ("");
    c.gridx = 1;
    c.gridy = 5;
    pane2.add(dollarValueLabel, c);



    ////////////////////////////////////////////////////////////////
    //Set up Pane 3   This is shown when licensing conditions have been met
    
    JPanel pane3 = new JPanel(new GridBagLayout());
    pane3.setBorder(BorderFactory.createRaisedBevelBorder());

     
    
    label = new JLabel("Doublespend?:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.insets = new Insets(5,5,5,5);  
    c.anchor = GridBagConstraints.LINE_END;
    pane3.add(label, c);

    
    doubleSpendLabel = new JLabel( "");
    if(this.doubleSpend){doubleSpendLabel.setForeground(Color.red);}
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;
    pane3.add(doubleSpendLabel, c);

    JLabel ltclabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("../../resources/main/ltc2.png")));
    //ltclabel.setPreferredSize(new Dimension(180, 180));
    
    c.gridx = 3;
    c.gridy = 0;
    c.gridwidth = 3;
    c.gridheight = 4;
    c.weightx = 1;
    c.weighty = 1;
    pane3.add(ltclabel, c);
    
    label = new JLabel("Elapsed time (hours):");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.LINE_END;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane3.add(label, c);
    
    elapsedTimeLabel =new JLabel (""); 
    c.gridx = 1;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane3.add(elapsedTimeLabel, c);

    
    label = new JLabel("Confirmations:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
    c.anchor = GridBagConstraints.LINE_END;
    pane3.add(label, c);


     confirmationsLabel =new JLabel (""); 
    c.gridx = 1;
    c.gridy = 2;
    c.anchor = GridBagConstraints.LINE_START;
    pane3.add(confirmationsLabel, c);

    label = new JLabel("Transferred value($):");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 3;
    c.anchor = GridBagConstraints.LINE_END;
    pane3.add(label, c);

    dollarValueLabel =new JLabel (""); //$ value 
    c.gridx = 1;
    c.gridy = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane3.add(dollarValueLabel, c);

    label = new JLabel("Transferred value(LTC):");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 4;
    c.anchor = GridBagConstraints.LINE_END;
    pane3.add(label, c);

    ltcValueLabel =new JLabel (""); //LTC value 
    c.gridx = 1;
    c.gridy = 4;
    c.anchor = GridBagConstraints.LINE_START;
    pane3.add(dollarValueLabel, c);



    
    JButton closeButton = new JButton("Close");
    closeButton.setMnemonic(KeyEvent.VK_C);
    closeButton.setActionCommand("close");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 3;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    closeButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        }));

    pane3.add(closeButton, c);


    ////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    
    cards = new JPanel(this.cl);
    
    cards.add(pane1, INTROPANEL);
    cards.add(pane2, UNLICENSEDPANEL);
    cards.add(pane3, LICENSEDPANEL);
    cl.show( cards , DialogLicenseManager.INTROPANEL);

    parentPane.add(licenseLabelPane, BorderLayout.NORTH);  //contains the "Licensed" status
    parentPane.add(cards, BorderLayout.CENTER);
  
    
    this.getContentPane().add(parentPane, BorderLayout.CENTER);
    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }





  /**
   * need to adress this stuff
      this.doubleSpendLabel.setText(Boolean.toString(this.doubleSpend));
      if (this.doubleSpend) {
        doubleSpendLabel.setForeground(Color.red);
      }

      this.elapsedTimeLabel.setText(Long.toString(this.deltaTime));
      if (deltaTime > this.expiresInHours) {
        elapsedTimeLabel.setForeground(Color.red);
      }

      this.confirmationsLabel.setText(Long.toString(this.numberOfConfirmations));
      if (this.numberOfConfirmations < this.requiredConfirmations) {
        confirmationsLabel.setForeground(Color.red);
      }

      this.dollarValueLabel.setText(Double.toString(this.paymentInDollars));
      if (this.paymentInDollars < this.cost) {
        dollarValueLabel.setForeground(Color.red);
      }

   */


  public void runCostCalculations(){
    /*
  private double cost;  
  private int unitsOfCost;    1 = dollars      2= LTC      3=SATOSHIS   
  private double costLTC;  
  private double costDollars;  
  private double costSatoshis;  


    switch(unitsOfCost){

    case DialogLicenseManager.DOLLARS:

    case DialogLicenseManager.LTC:

    case DialogLicenseManager.SATOSHIS: this.costSatoshis = this.cost;
      break;

      
    }

    */

    
  }

  

  public String getMerchantWalletID(){
    return this.merchantWalletID;
    
  }
  
  
}


// 8cea06d224b82adba65742673f4907d3c5423b93626800f8449d3acfc1717361
