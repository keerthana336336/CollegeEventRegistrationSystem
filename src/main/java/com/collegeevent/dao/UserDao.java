package com.collegeevent.dao;

import com.collegeevent.model.User;
import com.collegeevent.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao {
    private final JsonRepository<User> repo = DataStore.get().users;

    public User findById(String id) {
        if (id == null) return null;
        synchronized (DataStore.LOCK) {
            for (User u : repo.items()) if (id.equals(u.getId())) return repo.copy(u);
            return null;
        }
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        synchronized (DataStore.LOCK) {
            for (User u : repo.items()) if (email.equalsIgnoreCase(u.getEmail())) return repo.copy(u);
            return null;
        }
    }

    public List<User> findStudents() {
        synchronized (DataStore.LOCK) {
            List<User> out = new ArrayList<User>();
            for (User u : repo.items()) if (!u.isAdmin()) out.add(repo.copy(u));
            return out;
        }
    }

    public int countStudents() {
        synchronized (DataStore.LOCK) {
            int n = 0;
            for (User u : repo.items()) if (!u.isAdmin()) n++;
            return n;
        }
    }

    /**
     * Creates a STUDENT account (the role is always forced to STUDENT here).
     * @return null on success, otherwise the form field that is already taken ("email" or "studentId")
     */
    public String createStudent(User u) {
        synchronized (DataStore.LOCK) {
            for (User x : repo.items()) {
                if (u.getEmail().trim().equalsIgnoreCase(x.getEmail())) return "email";
                if (!x.isAdmin() && x.getStudentId() != null && x.getStudentId().equalsIgnoreCase(u.getStudentId())) return "studentId";
            }
            u.setId(UUID.randomUUID().toString());
            u.setRole(User.ROLE_STUDENT);
            u.setEmail(u.getEmail().trim().toLowerCase());
            u.setCreatedAt(DateUtil.nowStamp());
            repo.items().add(repo.copy(u));
            repo.save();
            return null;
        }
    }

    /** Updates name, student ID, department and year. @return null on success or "studentId" when taken / "notfound". */
    public String updateProfile(String userId, String name, String studentId, String department, int year) {
        synchronized (DataStore.LOCK) {
            User target = null;
            for (User x : repo.items()) {
                if (userId.equals(x.getId())) target = x;
                else if (!x.isAdmin() && x.getStudentId() != null && x.getStudentId().equalsIgnoreCase(studentId)) return "studentId";
            }
            if (target == null) return "notfound";
            target.setName(name);
            target.setStudentId(studentId);
            target.setDepartment(department);
            target.setYear(year);
            repo.save();
            return null;
        }
    }

    public boolean updatePasswordHash(String userId, String newHash) {
        synchronized (DataStore.LOCK) {
            for (User x : repo.items()) {
                if (userId.equals(x.getId())) {
                    x.setPasswordHash(newHash);
                    repo.save();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Makes sure the admin account with this fixed e-mail exists and uses this password.
     * Runs on every start-up, so the admin login always works with the configured details
     * (even if users.json was replaced or an older admin used a different e-mail).
     * @return true if the account was created or repaired.
     */
    public boolean ensureAdminAccount(String email, String name, String plainPassword) {
        synchronized (DataStore.LOCK) {
            for (User x : repo.items()) {
                if (email.equalsIgnoreCase(x.getEmail())) {
                    if (!x.isAdmin()) return false; // a student already owns this e-mail: never touch it
                    if (com.collegeevent.util.PasswordUtil.verify(plainPassword, x.getPasswordHash())) return false;
                    x.setPasswordHash(com.collegeevent.util.PasswordUtil.hash(plainPassword));
                    repo.save();
                    return true;
                }
            }
            User admin = new User();
            admin.setId(UUID.randomUUID().toString());
            admin.setName(name);
            admin.setEmail(email.trim().toLowerCase());
            admin.setStudentId("ADMIN");
            admin.setDepartment("Administration");
            admin.setYear(0);
            admin.setRole(User.ROLE_ADMIN);
            admin.setPasswordHash(com.collegeevent.util.PasswordUtil.hash(plainPassword));
            admin.setCreatedAt(DateUtil.nowStamp());
            repo.items().add(admin);
            repo.save();
            return true;
        }
    }

    /** Creates the first admin account if no admin exists yet. @return true if one was created. */
    public boolean ensureAdmin(String email, String name, String passwordHash) {
        synchronized (DataStore.LOCK) {
            for (User x : repo.items()) if (x.isAdmin()) return false;
            User admin = new User();
            admin.setId(UUID.randomUUID().toString());
            admin.setName(name);
            admin.setEmail(email);
            admin.setStudentId("ADMIN");
            admin.setDepartment("Administration");
            admin.setYear(0);
            admin.setRole(User.ROLE_ADMIN);
            admin.setPasswordHash(passwordHash);
            admin.setCreatedAt(DateUtil.nowStamp());
            repo.items().add(admin);
            repo.save();
            return true;
        }
    }
}
