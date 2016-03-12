package pl.edu.wat.wcy.dsk.votingappmobile.login;

import java.io.Serializable;


public class User implements Serializable {
    private Long id;
    private String firstName;
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
