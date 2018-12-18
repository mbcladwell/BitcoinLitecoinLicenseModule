package bllm;


import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
//import java.io.IOException;
import java.net.*;
import java.util.*;

import java.util.logging.*;


public class LicenseReaderWriter   {


  private  License lic;
  private DialogLicenseManager parent;


  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private SecretKey key64;
  private Cipher cipher;
  private String privateKey;
  private String licenseFileName;
  
  public LicenseReaderWriter( DialogLicenseManager _parent, String _licenseFileName, String _privateKey ) {
    this.parent = _parent;
    this.privateKey = _privateKey;
    this.licenseFileName = _licenseFileName;

    if( new File(licenseFileName).exists()){

      try {
	key64 = new SecretKeySpec( privateKey.getBytes() , "Blowfish");
	cipher = Cipher.getInstance("Blowfish");
	
      }catch (NoSuchAlgorithmException ex) {

      } catch (NoSuchPaddingException ex) {

      }
    }


  }

  public License readLicense(){

    try{
      cipher.init(Cipher.DECRYPT_MODE, key64);
	  
      CipherInputStream cipherInputStream =
	new CipherInputStream(new BufferedInputStream(new FileInputStream(licenseFileName)), cipher);
      ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
      SealedObject sealedObject = (SealedObject) inputStream.readObject();
      lic = (License) sealedObject.getObject(cipher);
      
      LOGGER.info("License read in LicenseReaderWriter.readLicense():  " + licenseFileName);	
      inputStream.close();

    }
    catch (IOException ex) {
      LOGGER.info("IOException is caught in LicenseManager post license validation");
    } catch (ClassNotFoundException ex) {
      LOGGER.info("ClassNotFoundException is caught in LicenseManager post license validation");
    } catch (IllegalBlockSizeException ex) {
      
    } catch (BadPaddingException ex) {
      
    } catch (InvalidKeyException ex) {
      
    }
    return lic;
  }

 

  public void writeLicense( License _lic ){
    License lic = _lic;
    try {   
      cipher.init(Cipher.ENCRYPT_MODE, key64);
      SealedObject sealedObject = new SealedObject(this.lic, cipher);  
      CipherOutputStream cipherOutputStream =
	new CipherOutputStream(
			       new BufferedOutputStream(new FileOutputStream(licenseFileName)), cipher);
      ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream);
      outputStream.writeObject(sealedObject);
      outputStream.close();
      LOGGER.info("License written with trial period start date: " + lic.getTrialStartDate());
    }
     catch (IOException ex) {
         
      LOGGER.info("IOException is caught in LicenseReaderWriter.writeLicense()");
    }  catch (IllegalBlockSizeException ex) {

    }  catch (InvalidKeyException ex) {

    } 
  }

}
 


