package cxptek.service.iml;

import cxptek.service.HashService;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.algo.XxHash;

import java.security.MessageDigest;

public class HashServiceImpl implements HashService {

    private static final String ALGORITHM = "MD5";

    @Override
    public int createMD5Hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = md.digest(key.getBytes());
            return Math.abs(hashBytes[0]);
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    @Override
    public long createXXHash(String key) {
        return XxHash.INSTANCE.applyAsLong(BytesStore.from(key));
    }
}
