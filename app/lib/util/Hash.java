package lib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Hash {

	public static String encode(String password, String algorithm) throws NoSuchAlgorithmException {
		byte[] hash = null;
		
		MessageDigest md = MessageDigest.getInstance(algorithm);
		hash = md.digest(password.getBytes());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hash.length; ++i) {
			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				sb.append(0);
				sb.append(hex.charAt(hex.length() - 1));
			} else {
				sb.append(hex.substring(hex.length() - 2));
			}
		}
		return sb.toString();
	}

}
