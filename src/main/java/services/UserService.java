package services;

import org.mindrot.jbcrypt.BCrypt;

import dao.UserDao;
import model.user.User;

public class UserService {
    private final UserDao userDao;
    private final EmailService emailService;

    public UserService(UserDao userDao, EmailService emailService) {
        this.userDao = userDao;
        this.emailService = emailService;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public User login(String loginKey, String password) {
        User user = userDao.findByEmailOrPhone(loginKey);
        if (user == null) {
            return null;
        }
        if (!"active".equals(user.getStatus())) {
            return null;
        }
        if (user.getPassword() != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean register(String name, String phone, String email, String password, String domain) {
        if (userDao.findByEmail(email) != null || userDao.findByPhone(phone) != null) {
            return false;
        }
        String token = java.util.UUID.randomUUID().toString();

        User user = new User();
        user.setFullName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
        user.setRole("user");
        user.setAuthProvider("local");
        user.setStatus("inactive");
        user.setVerifyToken(token);

        int id = userDao.insert(user);
        if (id > 0) {
            emailService.sendVerifyLink(email, token, domain);
            return true;
        }
        return false;
    }

    public User processSocialLogin(String email, String name, String firebase_uid, String provider) {
        User user = userDao.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setFirebaseUID(firebase_uid);
            newUser.setAuthProvider(provider);
            newUser.setRole("user");
            int newId = userDao.insert(newUser);
            newUser.setId(newId);
            return newUser;
        }
    }


}