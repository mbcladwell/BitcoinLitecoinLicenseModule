package bllm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.time.*;
import java.util.*;
//import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;
//import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.util.logging.*;

public class UnlicensedPanel extends JFrame {

  private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  static JButton button;
  static JLabel licenseKey;
  static JLabel picLabel;
  static JLabel label;
  static JTextField textField;
  
  static JButton okButton;
  static JButton cancelButton;
  private JLabel licenseLabel;
  private JLabel  doubleSpendLabel;
  private JLabel  elapsedTimeLabel;
  public JLabel  confirmationsLabel;
  private URL imageURL;
 

  private double requestedPayment;
  private String unitsOfRequestedPayment;
  private String merchantWalletID;
  private long requiredConfirmations;
  private int expiresInHours;
  private DialogLicenseManager parent;
  private LicenseManager lm;
  private String title;
  private boolean useTrialBanner;
  private int trialExpiresInDays;
  
  public UnlicensedPanel(DialogLicenseManager _parent,
			 boolean _useTrialBanner,
			 int _trialExpiresInDays,
			 double _requestedPayment,
			 String _unitsOfRequestedPayment,
			 String _merchantWalletID,
			 String _licenseID,
			 long _requiredConfirmations,
			 int _expiresInHours,
			 LicenseManager _lm,
			 String _title) {

    this.useTrialBanner = _useTrialBanner;
    this.trialExpiresInDays = _trialExpiresInDays;
    this.requestedPayment = _requestedPayment;
    this.unitsOfRequestedPayment = _unitsOfRequestedPayment;
    this.merchantWalletID = _merchantWalletID;
    this.requiredConfirmations = _requiredConfirmations;
    this.expiresInHours = _expiresInHours;
    this.lm = _lm;
    this.title = _title;
    this.parent = _parent;
    
    this.setLayout(new BorderLayout());
    // this.setTitle("Cryptocurrency License Module  " + LocalDate.now() );
    this.setResizable(true);
    this.setTitle(title);
  switch(unitsOfRequestedPayment){

    case "Bitcoin":
    imageURL = bllm.UnlicensedPanel.class.getResource("images/btc2.png");
    break;
    case "Litecoin":
    imageURL = bllm.UnlicensedPanel.class.getResource("images/ltc.png");
    break;
  }
  
  // ImageIcon imgIcon =   new ImageIcon( imageURL);
  // this.setIconImage(imgIcon.getImage());
 

    JPanel licenseLabelPane = new JPanel(new BorderLayout());
    licenseLabelPane.setBorder(BorderFactory.createTitledBorder(""));

    if(useTrialBanner){
      String dayORdays = (trialExpiresInDays == 1)? "day":"days";
      licenseLabel = new JLabel("Trial period expires in " + trialExpiresInDays + " " + dayORdays, JLabel.CENTER);
    }else{
      licenseLabel = new JLabel("Unlicensed", JLabel.CENTER);
      licenseLabel.setForeground( Color.red);
    }
    
    licenseLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
    licenseLabelPane.add(licenseLabel, BorderLayout.CENTER);
    this.add(licenseLabelPane, BorderLayout.NORTH);  

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

    label = new JLabel("Transfer " +  String.valueOf(new java.text.DecimalFormat("######.######").format(this.requestedPayment)) + " " + unitsOfRequestedPayment + " to wallet ID: ");
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
  
      ImageIcon clipImgIcon =  new ImageIcon(UnlicensedPanel.class.getResource("images/clipboard2.png"));
      clipboardButton.setIcon( new ImageIcon(clipImgIcon.getImage().getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH )) );
    } catch (Exception ex) {
      LOGGER.severe("Exception: " +  ex.getMessage());
    } 
    
    c.gridx = 4;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    clipboardButton.addActionListener(
        (new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      StringSelection stringSelection = new StringSelection( merchantWalletID );
	      
	      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	      clpbrd.setContents( stringSelection, null);
	    }
	  }));
    pane1.add(clipboardButton, c);

    JButton qrButton = new JButton();
    // clipboardButton.setMnemonic(KeyEvent.VK_Y);
    qrButton.setToolTipText("Generate QR code for transaction");
    
    try {   
  
      ImageIcon qrImgIcon =  new ImageIcon(UnlicensedPanel.class.getResource("images/qr.png"));
      qrButton.setIcon( new ImageIcon(qrImgIcon.getImage().getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH )) );
    } catch (Exception ex) {
      LOGGER.severe("Exception: " + ex.getMessage());
    } 
    
    c.gridx = 5;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    qrButton.addActionListener(
        (new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      new DialogQRCode( unitsOfRequestedPayment,  merchantWalletID, String.valueOf(new java.text.DecimalFormat("######.######").format(requestedPayment)));	     
	    }
	  }));
    pane1.add(qrButton, c);

    
    label = new JLabel("2.");
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane1.add(label, c);

    String labelText;
    switch((int)(this.requiredConfirmations)){
    case 0:
      labelText = new String("No waiting for confirmations required.");
      break;
    default:
      labelText = new String( "Wait for " + this.requiredConfirmations + " confirmations.");
      break;
    }

    
    label = new JLabel(labelText);
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
    okButton.putClientProperty( "parent", this.parent );
    okButton.setToolTipText("Extract transaction details from blockcypher.com");
    //c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    okButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    if( textField.getText() != null && !textField.getText().isEmpty()) {
	      lm.evaluateTransaction( textField.getText() );
	    } else{
	      JOptionPane.showMessageDialog( parent,
					     "Enter a valid transaction ID in the text box.",
					     "Invalid transaction",
					     JOptionPane.ERROR_MESSAGE);
	    }
	    dispose();
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
    c.gridwidth = 2;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    cancelButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //((JFrame)((JPanel)e.getSource()).getParent()).
	      dispose();
          }
        }));

    pane1.add(cancelButton, c);

    this.add(pane1, BorderLayout.CENTER); 
    this.pack();
     this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
      this.setVisible(true);
 

    
  }
}
