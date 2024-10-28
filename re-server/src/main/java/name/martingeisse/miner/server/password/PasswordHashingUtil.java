package name.martingeisse.miner.server.password;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class PasswordHashingUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    // prevent instantiation
    private PasswordHashingUtil() {
    }

    public static byte[] hashPassword(String password) {
        try {
            // see draft-irtf-cfrg-argon2-13 for these default values
            PreambleFields preambleFields = new PreambleFields();
            preambleFields.version = Argon2Parameters.ARGON2_VERSION_13;
            preambleFields.salt = new byte[16];
            secureRandom.nextBytes(preambleFields.salt);
            preambleFields.parallelism = 4;
            preambleFields.memoryAsKB = 64 * 1024;
            preambleFields.iterations = 1;
            preambleFields.outputLengthBytes = 32;

            byte[] actualHash = hash(preambleFields, password.getBytes(StandardCharsets.UTF_8));

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
                    dataOutputStream.writeInt(preambleFields.version);
                    dataOutputStream.writeInt(preambleFields.salt.length);
                    dataOutputStream.write(preambleFields.salt);
                    dataOutputStream.writeInt(preambleFields.parallelism);
                    dataOutputStream.writeInt(preambleFields.memoryAsKB);
                    dataOutputStream.writeInt(preambleFields.iterations);
                    dataOutputStream.writeInt(preambleFields.outputLengthBytes);
                    dataOutputStream.write(actualHash);
                    dataOutputStream.flush();
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("internal exception while generating password hash", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, byte[] storedPasswordHash) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(storedPasswordHash)) {
            try (DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
                PreambleFields preambleFields = new PreambleFields();
                preambleFields.version = dataInputStream.readInt();
                preambleFields.salt = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(preambleFields.salt);
                preambleFields.parallelism = dataInputStream.readInt();
                preambleFields.memoryAsKB = dataInputStream.readInt();
                preambleFields.iterations = dataInputStream.readInt();
                preambleFields.outputLengthBytes = dataInputStream.readInt();
                byte[] expectedHash = new byte[preambleFields.outputLengthBytes];
                dataInputStream.readFully(expectedHash);
                if (dataInputStream.read() != -1) {
                    return false;
                }
                byte[] actualHash = hash(preambleFields, inputPassword.getBytes(StandardCharsets.UTF_8));
                return MessageDigest.isEqual(expectedHash, actualHash);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static class PreambleFields {
        int version;
        byte[] salt;
        int parallelism;
        int memoryAsKB;
        int iterations;
        int outputLengthBytes;
    }

    private static byte[] hash(PreambleFields preambleFields, byte[] payload) {
        final Argon2Parameters bouncycastleParameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(preambleFields.version)
                .withSalt(preambleFields.salt)
                .withParallelism(preambleFields.parallelism)
                .withMemoryAsKB(preambleFields.memoryAsKB)
                .withIterations(preambleFields.iterations)
                .build();
        final Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(bouncycastleParameters);
        final byte[] result = new byte[preambleFields.outputLengthBytes];
        generator.generateBytes(payload, result);
        return result;
    }

}
