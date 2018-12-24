package bllm;

/** */
public class Main {

  public static void main(String[] args) {

    // dha.getLitecoinTransaction("8cea06d224b82adba65742673f4907d3c5423b93626800f8449d3acfc1717361");
    // cost           LTC wallet

    LicenseManager lm = new LicenseManager("./license.ser", "nszpx5U5Kt6d91JB3CW31n3SiNjSUzcZ");
    System.out.println("License status: " + lm.getLicenseStatus());
  }
}
