package bllm;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

public class TransactionFailedPanel extends JFrame {

  private static final long serialVersionUID = 1L;

  static JLabel licenseKey;
  static JLabel picLabel;
  static JLabel label;

  static JButton exitButton;
  static JButton returnButton;

  private JLabel doubleSpendLabel;
  private JLabel elapsedTimeLabel;
  private JLabel licenseIDLabel;
  private JLabel merchantWalletIDLabel;
  private JLabel requiredConfirmationsLabel;
  private JLabel actualConfirmationsLabel;
  private JLabel expiresInHoursLabel;
  private JLabel dollarCostLabel;
  private JLabel ltcCostLabel;
  private JLabel dollarSubmittedLabel;
  private JLabel ltcSubmittedLabel;
  private JLabel transactionIDLabel;

  private double dollarCost;
  private double cost;
  private double dollarSubmitted;
  private double costSubmitted;
  private boolean walletIDnotFound;

  private String merchantWalletID;
  private String licenseID;
  private String transactionID;
  private long requiredConfirmations;
  private long actualConfirmations;
  private int expiresInHours;
  private DialogLicenseManager parent;
  private boolean doubleSpend;
  private long deltaTime;
  private String title;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public TransactionFailedPanel(
      DialogLicenseManager parent,
      double dollarCost,
      double cost,
      String unitsOfCost,
      String merchantWalletID,
      String licenseID,
      String transactionID,
      long requiredConfirmations,
      int expiresInHours,
      boolean doubleSpend,
      long deltaTime,
      long actualConfirmations,
      double dollarSubmitted,
      double costSubmitted,
      boolean walletIDnotFound,
      String title) {

    this.dollarCost = dollarCost;
    this.cost = cost;
    this.merchantWalletID = merchantWalletID;
    this.requiredConfirmations = requiredConfirmations;
    this.expiresInHours = expiresInHours;
    this.parent = parent;
    this.licenseID = licenseID;
    this.setLayout(new BorderLayout());
    this.doubleSpend = doubleSpend;
    this.deltaTime = deltaTime;
    this.actualConfirmations = actualConfirmations;
    this.dollarSubmitted = dollarSubmitted;
    this.costSubmitted = costSubmitted;
    this.walletIDnotFound = walletIDnotFound;
    this.transactionID = transactionID;
    this.setTitle(title);

    JPanel licenseLabelPane = new JPanel(new BorderLayout());
    licenseLabelPane.setBorder(BorderFactory.createTitledBorder(""));

    JLabel licenseLabel = new JLabel("Transaction Failed", JLabel.CENTER);
    licenseLabel.setForeground(Color.red);
    licenseLabel.setFont(new Font("Serif", Font.BOLD, 25));
    licenseLabelPane.add(licenseLabel, BorderLayout.CENTER);
    this.add(licenseLabelPane, BorderLayout.NORTH);

    JPanel noticePane = new JPanel(new BorderLayout());

    label = new JLabel("Reason(s) for failure highlighted in red.", JLabel.CENTER);
    noticePane.add(label);
    this.add(noticePane, BorderLayout.CENTER);

    JPanel pane1 = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);

    pane1.setBorder(BorderFactory.createRaisedBevelBorder());
    LOGGER.info("begin failed panel; costSubmitted: " + this.costSubmitted);

    label = new JLabel("Merchant wallet ID:");
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    merchantWalletIDLabel = new JLabel(this.merchantWalletID);
    if (this.walletIDnotFound) {
      merchantWalletIDLabel.setForeground(Color.red);
    }
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(merchantWalletIDLabel, c);

    label = new JLabel("Transaction ID:");
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    transactionIDLabel = new JLabel(this.transactionID);
    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(transactionIDLabel, c);

    label = new JLabel("License ID:");
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    licenseIDLabel = new JLabel(this.licenseID);
    c.gridx = 1;
    c.gridy = 3;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(licenseIDLabel, c);

    label = new JLabel("Doublespend?:");
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    doubleSpendLabel = new JLabel(String.valueOf(this.doubleSpend));
    if (this.doubleSpend) {
      doubleSpendLabel.setForeground(Color.red);
    }
    c.gridx = 1;
    c.gridy = 4;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(doubleSpendLabel, c);

    label = new JLabel("Hours since transaction submitted:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    elapsedTimeLabel = new JLabel(String.valueOf(this.deltaTime));
    c.gridx = 1;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_START;
    if (this.deltaTime > this.expiresInHours) {
      elapsedTimeLabel.setForeground(Color.red);
    }

    pane1.add(elapsedTimeLabel, c);

    label = new JLabel("Limit:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    expiresInHoursLabel = new JLabel(String.valueOf(this.expiresInHours));
    c.gridx = 3;
    c.gridy = 5;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(expiresInHoursLabel, c);

    label = new JLabel("Confirmations:");
    c.gridx = 0;
    c.gridy = 6;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    actualConfirmationsLabel = new JLabel(String.valueOf(this.actualConfirmations));
    c.gridx = 1;
    c.gridy = 6;
    if (this.actualConfirmations < this.requiredConfirmations) {
      actualConfirmationsLabel.setForeground(Color.red);
    }

    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(actualConfirmationsLabel, c);

    label = new JLabel("Required:");
    c.gridx = 2;
    c.gridy = 6;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    requiredConfirmationsLabel = new JLabel(String.valueOf(this.requiredConfirmations));
    c.gridx = 3;
    c.gridy = 6;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(requiredConfirmationsLabel, c);

    label = new JLabel("Dollars submitted:");
    c.gridx = 0;
    c.gridy = 7;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    dollarSubmittedLabel = new JLabel(String.valueOf(this.dollarSubmitted));
    c.gridx = 1;
    c.gridy = 7;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(dollarSubmittedLabel, c);

    label = new JLabel("Dollar cost:");
    c.gridx = 2;
    c.gridy = 7;
    c.anchor = GridBagConstraints.LINE_END;
    pane1.add(label, c);

    dollarCostLabel = new JLabel(String.valueOf(this.dollarCost));
    c.gridx = 3;
    c.gridy = 7;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(dollarCostLabel, c);

    label = new JLabel(unitsOfCost + " submitted:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.LINE_END;
    c.gridx = 0;
    c.gridy = 8;
    pane1.add(label, c);

    ltcSubmittedLabel = new JLabel(String.valueOf(this.costSubmitted));
    c.gridx = 1;
    c.gridy = 8;
    c.anchor = GridBagConstraints.LINE_START;
    if (this.costSubmitted < this.cost) {
      ltcSubmittedLabel.setForeground(Color.red);
      dollarSubmittedLabel.setForeground(Color.red);
    }
    pane1.add(ltcSubmittedLabel, c);

    label = new JLabel(unitsOfCost + " cost:");
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 8;
    c.anchor = GridBagConstraints.LINE_END;

    pane1.add(label, c);

    ltcCostLabel = new JLabel(String.valueOf(this.cost));
    c.gridx = 3;
    c.gridy = 8;
    c.anchor = GridBagConstraints.LINE_START;
    pane1.add(ltcCostLabel, c);

    exitButton = new JButton("Cancel");
    exitButton.setMnemonic(KeyEvent.VK_C);
    c.gridx = 0;
    c.gridy = 9;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_END;
    exitButton.putClientProperty("parent", this.parent);
    exitButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // ((JFrame)((JButton)e.getSource()).getClientProperty( "parent" )).
            TransactionFailedPanel.this.dispose();
          }
        }));

    pane1.add(exitButton, c);

    returnButton = new JButton("Return");
    returnButton.setMnemonic(KeyEvent.VK_R);
    // returnButton.setActionCommand("cancel");
    returnButton.setEnabled(true);
    // returnButton.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx = 1;
    c.gridy = 9;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.LINE_START;
    returnButton.putClientProperty("parent", this.parent);
    returnButton.addActionListener(
        (new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            parent.displayUnlicensedPanel();
            TransactionFailedPanel.this.dispose();
          }
        }));

    pane1.add(returnButton, c);

    this.add(pane1, BorderLayout.SOUTH);

    this.pack();
    this.setLocation(
        (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
        (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    this.setVisible(true);
  }
}
