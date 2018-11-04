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

import java.time.format.DateTimeFormatter; 



public class LicensedPanel extends JFrame {

  private static final long serialVersionUID = 1L;

    static JButton button;
  static JLabel licenseKey;
  static JLabel picLabel;
  static JLabel label;
  private JLabel cryptoLabel;
 
 
  static JButton cancelButton;
 private JLabel  merchantWalletIDLabel;
  
 private JLabel  licenseIDLabel;
  private JLabel  transactionIDLabel;
  private JLabel  dollarValueLabel;
  private JLabel  costValueLabel;
  private JLabel  expiresInDaysLabel;
  private JLabel licenseGrantedDateLabel;
  

  private String merchantWalletID;
  private String licenseID;
  private String transactionID;
  
  private DialogLicenseManager parent;
  private double costSubmitted;
  private String unitsOfCost;
  private double dollarSubmitted;
  private long licenseDaysRemaining;
  private LocalDate licenseGrantedDate;
  
  public LicensedPanel(DialogLicenseManager parent,
			  String merchantWalletID,
		       String licenseID,
		       String transactionID,
		       LocalDate licenseGrantedDate,
		       long licenseDaysRemaining,
		       double costSubmitted,
		       String unitsOfCost,
		       double dollarSubmitted) {

   
    this.merchantWalletID = merchantWalletID;
    this.licenseID = licenseID;
    this.transactionID = transactionID;
    this.parent = parent;
    this.costSubmitted = costSubmitted;
    this.dollarSubmitted = dollarSubmitted;
    this.licenseDaysRemaining = licenseDaysRemaining;
    this.setLayout(new BorderLayout());
    this.licenseGrantedDate = licenseGrantedDate;

    JPanel licenseLabelPane = new JPanel(new BorderLayout());
    licenseLabelPane.setBorder(BorderFactory.createTitledBorder(""));

    JLabel licenseLabel = new JLabel("Licensed", JLabel.CENTER);
    licenseLabel.setForeground( Color.green);
    licenseLabel.setFont(new Font("Serif", Font.BOLD, 25));
    licenseLabelPane.add(licenseLabel, BorderLayout.CENTER);
    this.add(licenseLabelPane, BorderLayout.NORTH);  

    JPanel pane1 = new JPanel(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
 
    pane1.setBorder(BorderFactory.createRaisedBevelBorder());

     
    
    label = new JLabel("Merchant Wallet ID: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.insets = new Insets(5,5,5,5);  
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    
    merchantWalletIDLabel = new JLabel( this.merchantWalletID);
    //if(this.doubleSpend){doubleSpendLabel.setForeground(Color.red);}
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(merchantWalletIDLabel, c);

    
  switch(unitsOfCost){
    case "Bitcoin":
    cryptoLabel = new JLabel(new ImageIcon(LicensedPanel.class.getResource("images/btc2.png")));
    break;
    case "Litecoin":
    cryptoLabel = new JLabel(new ImageIcon(LicensedPanel.class.getResource("images/ltc2.png")));
    break;
  }    
    c.gridx = 3;
    c.gridy = 0;
    c.gridwidth = 3;
    c.gridheight = 6;
    c.weightx = 1;
    c.weighty = 1;
    pane1.add(cryptoLabel, c);
    
    label = new JLabel("Transaction ID: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.LINE_END;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    pane1.add(label, c);
    
    transactionIDLabel =new JLabel (this.transactionID); 
    c.gridx = 1;
    c.gridy = 1;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(transactionIDLabel, c);

    
    label = new JLabel("License ID:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);


    licenseIDLabel =new JLabel (this.licenseID); 
    c.gridx = 1;
    c.gridy = 2;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(licenseIDLabel, c);


        label = new JLabel("Transaction date: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 3;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-LLLL-dd");
    String formattedString = this.licenseGrantedDate.format(formatter);
    licenseGrantedDateLabel =new JLabel ( formattedString ); 
    c.gridx = 1;
    c.gridy = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(licenseGrantedDateLabel, c);
    
        label = new JLabel("License expires in: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 4;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    expiresInDaysLabel =new JLabel (String.valueOf(this.licenseDaysRemaining) + " days"); 
    c.gridx = 1;
    c.gridy = 4;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(expiresInDaysLabel, c);
    
    label = new JLabel("Litecoin transaction value: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    costValueLabel =new JLabel (unitsOfCost + " " + String.valueOf(this.costSubmitted));  
    c.gridx = 1;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(costValueLabel, c);

    label = new JLabel("Dollar transaction  value: ");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 6;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    dollarValueLabel =new JLabel ("$" + String.valueOf(this.dollarSubmitted));  
    c.gridx = 1;
    c.gridy = 6;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(dollarValueLabel, c);

    
    JButton closeButton = new JButton("Close");
    closeButton.setMnemonic(KeyEvent.VK_C);
    //closeButton.setActionCommand("close");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 3;
    c.gridy = 6;
    c.gridwidth = 1;
    c.gridheight = 1;
        closeButton.putClientProperty( "parent", this );

    closeButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	    //      ((JFrame)((JButton)e.getSource()).getClientProperty( "parent" )).dispose();
	    //      ((JFrame)((JButton)e.getSource()).getClientProperty( "parent" )).dispose();
	    dispose();
          }
        }));

    pane1.add(closeButton, c);

    this.add(pane1, BorderLayout.CENTER);
        this.pack();
     this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
      this.setVisible(true);

    
  }
}
