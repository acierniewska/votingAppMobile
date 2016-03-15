package pl.edu.wat.wcy.dsk.votingappmobile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Anna Cierniewska on 13.03.2016.
 */
public class Result implements Serializable {
    private String question;
    private Map<String, BigDecimal> answers;
}
