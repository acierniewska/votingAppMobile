package pl.edu.wat.wcy.dsk.votingappmobile;

import java.io.Serializable;

/**
 * Created by Anna Cierniewska on 13.03.2016.
 */
public class Answer implements Serializable {
    private int id;
    private String answer;
    private int numberOfVotes;
    private int percent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
