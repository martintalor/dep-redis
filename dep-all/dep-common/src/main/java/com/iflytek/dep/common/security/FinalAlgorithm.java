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

public interface FinalAlgorithm {
	
	public String getName();

	public String getTransformation();
	
	public Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
	
	public byte[] encryptWithPubKey(byte[] bt, PublicKey pub) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException;
	
	public byte[] decrptWithPrivateKey(byte[] bt, PrivateKey pvt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException;
}
