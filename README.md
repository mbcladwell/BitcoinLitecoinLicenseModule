## Litecoin License Module

Noninteractive license management for Java developers


The workflow for obtaining a license looks like this:

![](extra/unlicensed.png)
![](extra/fig1.png)
![](extra/licensedview.png)

## Download LLM from Github

Place the jar file on your classpath. If you are working with Gradle, you can install LLM in your local repository and make LLM a dependency.  First add to the local repository:

```bash
       >mvn install:install-file -Dfile=build/libs/llm-0.1.jar -DgroupId=net.stihie -DartifactId=llm -Dversion=0.1 -Dpackaging=jar
```

Check your ./.m2 directory and be sure it is there.  Then in your gradle.build file:


```java
repositories {
     //....
     mavenLocal()
}

dependencies {
	//.......
       compile 'net.stihie:llm:0.1'
}
```


Note that the license file has to be handled separately. Generate a license file with the [Litecoin License Manager License Key Generator](https://github.com/mbcladwell/LLMLitecoinLicenseGenerator) and copy the license to a directory where the user has read/write access.  The license file <code>license.ser</code> should not be packaged in your applications jar file.

## Integrate LLM into your application

Provide a menu item that will launch LLM:

![HelpLicense](extra/helplicense.png)

In the ActionListener of your menu item, launch the main window for LLM

```java

    menuItem = new JMenuItem("License", KeyEvent.VK_L);
    menuItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new llm.DialogLicenseManager(/path/to/readwriteaccessible/license.ser);
          }
        });
    menu.add(menuItem);
```

The above code provides a dialog box that the user can use to monitor license parameters and provide license fees.  In addition to the above, with each startup of your application you will want to monitor license status.  Do so with:

```java

    LicenseManager lm = new LicenseManager(/path/to/readwriteaccessible/license.ser);
    int licenseStatus = lm.getLicenseStatus();  //1:Unlicensed  2:Trial period  3:Licensed
```

The initial instantiation of <code>LicenseManager</code> will write the current date to the license file and initiate the trial period, if one was provided.  Future instantiations will calculate days remaining in either the trial period or license period, whichever is in effect.  Payment of a license fee will rewrite the <code>license.ser</code> file with the payment date, which is used in future instantiations to calculate days remaining in the license.






