package com.iflytek.dep.common.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class FileEncryptImpl implements FileEncrypt {

	private String inFilePath;
	private String outFilePath;
	private String pubKeyPath;
	private static SecureRandom srandom = new SecureRandom();
	
	public FileEncryptImpl(String inFilePath, String outFilePath, String pubKeyPath) {
		this.inFilePath = inFilePath;
		this.outFilePath = outFilePath;
		this.pubKeyPath = pubKeyPath;
	}

	@Override
	public void encrypt() throws EncryptException {
		
		FileInputStream in = null;
		FileOutputStream out = null;
				
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(this.pubKeyPath));
			X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey pub = kf.generatePublic(ks);
			
			in = new FileInputStream(this.inFilePath);
			out = new FileOutputStream(outFilePath  + "/" + EncryptUtils.getFileName(inFilePath));
			
			UpdateAlgorithm updateAlgorithm = new AESAlgorithm();
			SecretKey skey = updateAlgorithm.generateKey();
			
			byte[] iv = new byte[updateAlgorithm.getDefaultKeySize() / 8];
			srandom.nextBytes(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			
			RSAAlgorithm rsaAlgorithm = new RSAAlgorithm();
			byte[] encodedKey = rsaAlgorithm.encryptWithPubKey(skey.getEncoded(), pub);
			out.write(encodedKey);
			out.write(iv);
			
//			AESAlgorithm aesAlgorithm = new AESAlgorithm();
			updateAlgorithm.encrypt(in, out, skey, ivspec);
			
		} catch (NoSuchAlgorithmException | IOException 
				| InvalidKeySpecException | InvalidKeyException | NoSuchProviderException
				| IllegalBlockSizeException | BadPaddingException
				| NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			throw new EncryptException(e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	

}
