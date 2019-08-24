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
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESAlgorithm implements UpdateAlgorithm {

	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String NAME = "AES";
	public static final int DEFAULT_SIZE = 128;
	
	public AESAlgorithm() {
	}
	
	public void encrypt(FileInputStream in, FileOutputStream out, SecretKey skey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
		Cipher ci = getCipher();
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		byte[] ibuf = new byte[1024];
		int len;
		long completedSize = 0L;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			completedSize += obuf.length;
			if (obuf != null)
				out.write(obuf);
			System.out.println("AES Encyption Size:" + completedSize);
		}
		byte[] obuf = ci.doFinal();
		if (obuf != null)
			out.write(obuf);
	}
	
	public void decrypt(FileInputStream in, FileOutputStream out, SecretKey skey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
		Cipher ci = getCipher();
		ci.init(Cipher.DECRYPT_MODE, skey, ivspec);
		byte[] ibuf = new byte[1024];
		int len;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			if (obuf != null)
				out.write(obuf);
		}
		byte[] obuf = ci.doFinal();
		if (obuf != null)
			out.write(obuf);
	}
	
	@Override
	public String getName() {
		return NAME;
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
	public SecretKey generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance(NAME);
		kgen.init(DEFAULT_SIZE);
		return kgen.generateKey();
	}

	@Override
	public int getDefaultKeySize() {
		return DEFAULT_SIZE;
	}

	@Override
	public SecretKeySpec getSecretKeySpec(byte[] key) {
		return new SecretKeySpec(key, getName());
	}
}
