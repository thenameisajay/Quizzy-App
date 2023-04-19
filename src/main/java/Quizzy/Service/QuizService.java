package main.java.Quizzy.Service;

import main.java.Quizzy.Model.Quiz;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizService implements Serializable {

    private Map<Integer, Quiz> quizMap = new HashMap<>();

    private ArrayList<Integer> quizIDS = new ArrayList<>();

    public void addQuestion(int quizBoardID, String question, String correctAnswer, float marks) {
        // Initialise the question number to 1
        int questionNumber = 1;
        while (quizIDS.contains(questionNumber)) {
            questionNumber += 1;
        }
        quizIDS.add(questionNumber);
        Quiz quiz = new Quiz(quizBoardID, questionNumber, question, correctAnswer, marks);
        quizMap.put(questionNumber, quiz);
    }


    public void deleteQuestion(int quizBoardID, int quizID) {
        if (quizID == 0) {
            throw new IllegalArgumentException("Quiz ID cannot be 0");
        } else if (!quizIDS.contains(quizID)) {
            throw new IllegalArgumentException("Quiz ID does not exist");
        }
        if (quizMap.get(quizID).getQuizBoardID() != quizBoardID) {
            throw new IllegalArgumentException("Quiz ID does not exist");
        }
        quizMap.remove(quizID);
        // Remove the question number from the list of question numbers in the array list
        quizIDS.remove((Integer) quizID);

    }


    public Map<Integer, Quiz> viewAllQuestions(int quizBoardID) {
        Map<Integer, Quiz> localData = new HashMap<>();
        // Check for questions with the same quizBoardID
        for (Map.Entry<Integer, Quiz> entry : quizMap.entrySet()) {
            if (entry.getValue().getQuizBoardID() == quizBoardID) {
                localData.put(entry.getKey(), entry.getValue());
            }
        }

        return localData;
    }

    public void saveData() throws IOException {
        FileOutputStream f = new FileOutputStream("src/resources/QuestionData.ser");
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(quizMap);
        o.close();
        f.close();
    }

    public void loadData() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream("src/resources/QuestionData.ser");
        ObjectInputStream oi = new ObjectInputStream(fi);
        quizMap = (Map<Integer, Quiz>) oi.readObject();
        quizIDS = new ArrayList<>(quizMap.keySet());
        oi.close();
        fi.close();
    }


}