package org.questionsandanswers.util;

import forum.ForumOuterClass;

import java.util.Objects;

public class Util {
    public static void clearConsole() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    public static boolean isEmpty(ForumOuterClass.QuestionList questionList) {
        return Objects.isNull(questionList) || questionList.isInitialized();
    }
}
