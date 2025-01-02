package cxptek.service.iml;

import cxptek.service.HashService;

import java.security.MessageDigest;

public class MD5HashServiceImpl implements HashService {

    private static final String ALGORITHM = "MD5";

    @Override
    public int hash(String keyData) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = md.digest(keyData.getBytes());
            return Math.abs(hashBytes[0]);
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
