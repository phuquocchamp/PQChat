package com.example.pqchatclient.Utils;

import com.pkslow.ai.AIClient;
import com.pkslow.ai.GoogleBardClient;
import com.pkslow.ai.domain.Answer;
import com.pkslow.ai.domain.AnswerStatus;

public class BardAPI {
    public static void main(String[] args) {
        String token = "fQipN96Yc0iCmQI0qfR9Q6uLXIXKClrhVDjUSo9JYCtr1JhovdO3bJMy4tLvp_3DGX7rNQ." +";"
                    + "sidts-CjEBPVxjSrNUche_fcNWs-6mSpc5_D4aC4913st-A02nBfAXBZl1PEs_1vXj41X4YW-HEAA";
        AIClient client = new GoogleBardClient(token);
        Answer answer = client.ask("can you show me a picture of clock?");

        printChosenAnswer(answer);


    }
    private static void printChosenAnswer(Answer answer) {
        if (answer.getStatus() == AnswerStatus.OK) {
            System.out.println("Markdown Output: " +  answer.markdown());
        } else {
            System.out.println("No Answer: " + answer);
        }
    }

}
