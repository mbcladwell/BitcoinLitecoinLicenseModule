package bllm;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.util.*;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;



import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class DialogQRCode extends JFrame  {

  private static final long serialVersionUID = 1L;
  //private JPanel panel;

  public DialogQRCode(String unitsOfCost, String walletid, String amountOfCrypto  ) {
    

    String qrCodeText = unitsOfCost +  " " + walletid +  "?amount=" + amountOfCrypto;
    try{
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 350, 350);
     
         ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	 MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
	 byte[] pngData = pngOutputStream.toByteArray();
	   java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(new  ByteArrayInputStream(pngData));
	   JPanel qrPanel = new JPanel();
	  JLabel qrLabel = new JLabel(new ImageIcon(bi));
           qrPanel.add(qrLabel);
	   JPanel textPanel = new JPanel();
	   JLabel textLabel = new JLabel("<html><body>litecoin: " + walletid + "<br>amount=" + amountOfCrypto + "</body></html>");
	   textPanel.add(textLabel);
	   this.add(qrPanel, BorderLayout.CENTER);
	   this.add(textPanel, BorderLayout.SOUTH);
	   this.setTitle("QR code for transfer of funds");
	   this.pack();
	   this.setLocation(
			    (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
			    (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
	   this.setVisible(true);
 
	   
    } catch(WriterException we){
      
    }
    catch(IOException ioe){
      
    }
  }
}


