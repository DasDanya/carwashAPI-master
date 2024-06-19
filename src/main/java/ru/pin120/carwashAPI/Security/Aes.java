package ru.pin120.carwashAPI.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Класс Aes предоставляет методы для шифрования и дешифрования данных с использованием алгоритма AES.
 */
@Component
public class Aes {
    @Autowired
    private Environment environment;
    private static final String ALGORITHM = "AES";

    /**
     * Шифрует данные с использованием алгоритма AES
     *
     * @param data строка данных для шифрования
     * @return зашифрованная строка в формате Base64
     * @throws NoSuchAlgorithmException если алгоритм шифрования не найден
     * @throws InvalidKeyException если ключ шифрования недействителен
     * @throws IllegalBlockSizeException если размер блока недействителен
     * @throws BadPaddingException если заполнение неверно
     * @throws NoSuchPaddingException если схема заполнения не найдена
     */
    public String encrypt(String data) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(environment.getProperty("AES_SECRET_KEY").getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Дешифрует данные, зашифрованные с использованием алгоритма AES
     *
     * @param encryptedData зашифрованная строка в формате Base64
     * @return расшифрованная строка данных
     * @throws NoSuchAlgorithmException если алгоритм шифрования не найден
     * @throws InvalidKeyException если ключ шифрования недействителен
     * @throws NoSuchPaddingException если схема заполнения не найдена
     * @throws IllegalBlockSizeException если размер блока недействителен
     * @throws BadPaddingException если заполнение неверно
     */
    public String decrypt(String encryptedData) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(environment.getProperty("AES_SECRET_KEY").getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
