package com.iflytek.dep.common.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public interface UpdateAlgorithm {
	
	public String getName();

	public String getTransformation();
	
	public int getDefaultKeySize();
	
	public SecretKeySpec getSecretKeySpec(byte[] key);
	
	public SecretKey generateKey() throws NoSuchAlgorithmException, NoSuchProviderException;
	
	public Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
	
	public void encrypt(FileInputStream in, FileOutputStream out, SecretKey skey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException;
	
	public void decrypt(FileInputStream in, FileOutputStream out, SecretKey skey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException;
}
