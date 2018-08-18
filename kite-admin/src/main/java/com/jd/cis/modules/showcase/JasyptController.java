package com.jd.cis.modules.showcase;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping(value = "/showcase")
public class JasyptController {

    private static Logger logger = LoggerFactory.getLogger(JasyptController.class);

    @Autowired
    private StringEncryptor stringEncryptor;

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";


    public static boolean isEncryptedValue(final String value) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(ENCRYPTED_VALUE_PREFIX) &&
                trimmedValue.endsWith(ENCRYPTED_VALUE_SUFFIX));
    }

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(
                ENCRYPTED_VALUE_PREFIX.length(),
                (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }

    @RequestMapping(value = "/encrypt", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String encrypt(@RequestParam("text") String text) {
        String encrypted = stringEncryptor.encrypt(text.trim());
        logger.info("ORIGINAL: " + text);
        logger.info("ENCRYPTED: " + encrypted);
        logger.info("DECRYPTED: " + stringEncryptor.decrypt(encrypted));
        return String.format("ENC(%s)", encrypted);
    }

    @RequestMapping(value = "/decrypt", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String decrypt(@RequestParam("text") String text) {
        String decrypted = stringEncryptor.decrypt(isEncryptedValue(text) ? getInnerEncryptedValue(text) : text);
        logger.info("ORIGINAL: " + text);
        logger.info("DECRYPTED: " + decrypted);
        logger.info("ENCRYPTED: " + String.format("ENC(%s)", stringEncryptor.encrypt(decrypted)));
        return decrypted;
    }
}
