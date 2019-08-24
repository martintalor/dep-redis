package com.iflytek.dep.common.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAAlgorithm implements FinalAlgorithm {

	private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	private static final String NAME = "RSA";
	
	public RSAAlgorithm() {
	}
	
	public byte[] encryptWithPubKey(byte[] bt, PublicKey pub) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, pub);
		return cipher.doFinal(bt);
	}
	
	public byte[] decrptWithPrivateKey(byte[] bt, PrivateKey pvt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
		Cipher cipher = getCipher();
		cipher.init(Cipher.DECRYPT_MODE, pvt);
		return cipher.doFinal(bt);
	}

	@Override
	public String getTransformation() {
		return TRANSFORMATION;
	}

	@Override
	public Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
		return Cipher.getInstance(getTransformation());
	}
	
	@Override
	public String getName() {
		return NAME;
	}

}
