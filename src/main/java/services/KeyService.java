package services;

import dao.KeyDao;

public class KeyService {
    private final KeyDao keyDao;

    public KeyService() {
        this.keyDao = new KeyDao();
    }

    public boolean registerUserKey(int userId, String publicKey) {
        if (publicKey == null || publicKey.trim().isEmpty()) {
            return false;
        }
        return keyDao.registerNewKey(userId, publicKey.trim());
    }
}