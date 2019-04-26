package io.sapl.ethereum;

import org.apache.tools.ant.DirectoryScanner;

public class EthServices {
	
	public static String getUserWallet(String user, String keystore) {
		DirectoryScanner scanner = new DirectoryScanner();
    	scanner.setIncludes(new String[]{"*" + user.substring(2)});
    	scanner.setBasedir(keystore);
    	scanner.setCaseSensitive(false);
    	scanner.scan();
    	String[] files = scanner.getIncludedFiles();
    	String userWallet = keystore + files[0];
    	return userWallet;
	}

}
