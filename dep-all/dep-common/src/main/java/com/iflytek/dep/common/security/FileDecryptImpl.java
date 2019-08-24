package com.iflytek.dep.common.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class FileDecryptImpl implements FileDecrypt {

    private String inFilePath;
    private String outFilePath;
    private String privateKeyPath;

    public FileDecryptImpl(String inFilePath, String outFilePath, String privateKeyPath) {
        this.inFilePath = inFilePath;
        this.outFilePath = outFilePath;
        this.privateKeyPath = privateKeyPath;
    }

    @Override
    public void decrypt() throws DecryptException {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {

            byte[] bytes = Files.readAllBytes(Paths.get(this.privateKeyPath));
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pvt = kf.generatePrivate(ks);

            in = new FileInputStream(this.inFilePath);
            String newFileName = EncryptConstant.DECRYPT_PREFIX + EncryptUtils.getFileName(this.inFilePath);
            out = new FileOutputStream(this.outFilePath + "/" + newFileName);

            SecretKeySpec skey = null;

            byte[] b = new byte[256];
            in.read(b);

            RSAAlgorithm rsaAlgorithm = new RSAAlgorithm();
            UpdateAlgorithm updateAlgorithm = new AESAlgorithm();

            byte[] keyByte = rsaAlgorithm.decrptWithPrivateKey(b, pvt);
            skey = updateAlgorithm.getSecretKeySpec(keyByte);


            byte[] iv = new byte[updateAlgorithm.getDefaultKeySize() / 8];
            in.read(iv);

            IvParameterSpec ivspec = new IvParameterSpec(iv);

            updateAlgorithm.decrypt(in, out, skey, ivspec);

        } catch (NoSuchAlgorithmException | IOException
                | InvalidKeySpecException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            throw new DecryptException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

    }

}
