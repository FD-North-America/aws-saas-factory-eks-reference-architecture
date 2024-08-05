package com.amazonaws.saas.eks.service;

public interface EncryptionService {
    String encrypt(String data) throws Exception;
    String decrypt(String data) throws Exception;
}
