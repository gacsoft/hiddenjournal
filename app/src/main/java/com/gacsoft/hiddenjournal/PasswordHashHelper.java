package com.gacsoft.hiddenjournal;

import android.util.Base64;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Gacsoft on 8/8/2016.
 */
public class PasswordHashHelper {

    //we only have one user, generating salt is not going to help
    private final static byte[] SALT = { 23, 61, 34, 51, 14, 65, 95, 32, 12, 34, 53 ,16, 75, 53, 53, 74};

    static String getHash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), SALT, 128, 256);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } //TODO do something useful here
        catch (java.security.NoSuchAlgorithmException e) {{return "";}}
        catch (java.security.spec.InvalidKeySpecException e) {return "";}
    }
}
