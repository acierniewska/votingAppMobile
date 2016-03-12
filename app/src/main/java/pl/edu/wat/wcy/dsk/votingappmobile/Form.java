package pl.edu.wat.wcy.dsk.votingappmobile;

import java.io.Serializable;
import java.util.Map;

public class Form implements Serializable {
    private String question;
    private Map<Integer, String> answers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
    }
}
