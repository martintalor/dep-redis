package com.iflytek.dep.common.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SM4Algorithm implements UpdateAlgorithm {
	private static final String TRANSFORMATION = "SM4/ECB/PKCS5Padding";
	private static final String NAME = "SM4";
	public static final int DEFAULT_SIZE = 128;
	
	public SM4Algorithm() {
	}
	
	static {
        Security.addProvider(new BouncyCastleProvider());
    }
	
	@Override
	public Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
		Cipher cipher = Cipher.getInstance(NAME, BouncyCastleProvider.PROVIDER_NAME);
		return cipher;
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
	public int getDefaultKeySize() {
		return DEFAULT_SIZE;
	}

	@Override
	public SecretKey generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kg = KeyGenerator.getInstance(NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(getDefaultKeySize(), new SecureRandom());
        return kg.generateKey();
	}

	@Override
	public void encrypt(FileInputStream in, FileOutputStream out, SecretKey skey, IvParameterSpec ivspec)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException,
			NoSuchProviderException {
		Cipher ci = getCipher();
		Key sm4Key = new SecretKeySpec(skey.getEncoded(), NAME);
		ci.init(Cipher.ENCRYPT_MODE, sm4Key);
		byte[] ibuf = new byte[1024];
		int len;
		long completedSize = 0L;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			completedSize += obuf.length;
			if (obuf != null)
				out.write(obuf);
			System.out.println("SM4 Encyption Size:" + completedSize);
		}
		byte[] obuf = ci.doFinal();
		if (obuf != null)
			out.write(obuf);
	}
	
	@Override
	public SecretKeySpec getSecretKeySpec(byte[] key) {
		return new SecretKeySpec(key, getName());
	}
}
