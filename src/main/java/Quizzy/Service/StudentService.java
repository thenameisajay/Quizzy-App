package main.java.Quizzy.Service;

import main.java.Quizzy.Model.AccountType;
import main.java.Quizzy.Model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentService implements Serializable {

    Map<String, Student> students = new HashMap<>();

    public void register(String fullName, String email, String username, String sha256, AccountType accountType, ArrayList<String> coursesEnrolled, Date dateCreated) {
        // Check for empty string in fullName
        if (fullName.isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        // Check for duplicate username
        if (students.containsKey(username.toLowerCase())) {
            throw new IllegalArgumentException("Username already exists");
        }
        // Create a new HashMap for quizScores for each student
        Map<Integer, Float> quizScores = new HashMap<>();
        Student student = new Student(fullName.toLowerCase(), email.toLowerCase(), sha256, username.toLowerCase(), accountType, coursesEnrolled, dateCreated, quizScores);
        students.put(username.toLowerCase(), student);

    }

    public Map<String, Student> validateLogin(String userName, String password) {
        if (students.isEmpty()) {
            throw new IllegalArgumentException("No users found");

        }
        Map<String, Student> studentInfo = new HashMap<>();
        // CHECK IF the USER-NAME OR PASSWORD IS EMPTY
        if (userName.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }
        //Check if userName and password match
        if (students.containsKey(userName.toLowerCase())) {
            if (students.get(userName.toLowerCase()).getPassword().equals(password)) {
                studentInfo.put(userName.toLowerCase(), students.get(userName.toLowerCase()));
            }
        } else {
            throw new IllegalArgumentException("Username or password is incorrect");
        }
        return studentInfo;
    }

    public void saveData() throws IOException {
        FileOutputStream f = new FileOutputStream("src/resources/studentData.ser");
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(students);
        o.close();
        f.close();
    }

    public void loadData() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream("src/resources/studentData.ser");
        ObjectInputStream oi = new ObjectInputStream(fi);
        students = (Map<String, Student>) oi.readObject();
        oi.close();
        fi.close();
    }

    public String hashPassword(String password) {
        //Check for empty string
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String hashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm");
        }
        return hashedPassword;
    }

    public void validatePassword(String userName, String currentPassword) {
        if (currentPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String hashedPassword = hashPassword(currentPassword);
        if (!students.get(userName.toLowerCase()).getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("Password is incorrect");
        }
    }

    public void changePassword(String userName, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        if (newPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // Check if old password and new password are the same
        if (students.get(userName.toLowerCase()).getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        } else {
            students.get(userName.toLowerCase()).setPassword(hashedPassword);
        }
    }

    public void deleteAccount(String userName) {
        if (students.containsKey(userName.toLowerCase())) {
            students.remove(userName.toLowerCase());
        } else {
            throw new IllegalArgumentException("Username does not exist");
        }

    }

    public ArrayList<String> getStudentsByCourse(String courseName) {
        ArrayList<String> students = new ArrayList<>();
        for (Map.Entry<String, Student> entry : this.students.entrySet()) {
            if (entry.getValue().getCoursesEnrolled().contains(courseName.toLowerCase())) {
                students.add(entry.getKey());
            }
        }
        return students;
    }
}
