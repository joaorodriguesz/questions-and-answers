package org.questionsandanswers.client;

import forum.ForumOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.questionsandanswers.util.Util;

import java.util.Scanner;

public class ForumClient {
    private final ManagedChannel channel;
    private final forum.ForumGrpc.ForumBlockingStub blockingStub;

    public ForumClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = forum.ForumGrpc.newBlockingStub(channel);
    }

    public void listQuestions() {
        Util.clearConsole();
        System.out.println("Questions:");
        System.out.println("================================");
        ForumOuterClass.QuestionList questionList = blockingStub.listQuestions(ForumOuterClass.Empty.newBuilder().build());

        for (ForumOuterClass.Question question : questionList.getQuestionsList()) {
            System.out.println("ID: " + question.getId());
            System.out.println("QUESTION: " + question.getText());

            if (question.getAnswersCount() > 0) {
                System.out.println("ANSWERS:[");
                for (int i = 0; i < question.getAnswersList().size(); i++) {
                    System.out.println("    [" + (i + 1) + "] => " + question.getAnswersList().get(i));
                }
                System.out.println("]");
            }

            System.out.println("================================");
        }
    }

    public void addQuestion(String questionText) {
        Util.clearConsole();
        ForumOuterClass.Question question = ForumOuterClass.Question.newBuilder().setText(questionText).build();
        blockingStub.addQuestion(question);
        System.out.println("Question added successfully.");
    }

    public void answerQuestion(String questionId, String answerText) {
        Util.clearConsole();
        ForumOuterClass.AnswerRequest request = ForumOuterClass.AnswerRequest.newBuilder()
                .setQuestionId(questionId)
                .setAnswerText(answerText)
                .build();
        blockingStub.answerQuestion(request);
        System.out.println("Answer added successfully.");
    }

    public static void main(String[] args) {
        ForumClient client = new ForumClient("localhost", 8080);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. List questions\n2. Add question\n3. Answer question\n4. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline character
            switch (option) {
                case 1:
                    client.listQuestions();
                    break;
                case 2:
                    System.out.print("Enter question text: ");
                    String questionText = scanner.nextLine();
                    client.addQuestion(questionText);
                    break;
                case 3:
                    System.out.print("Enter question ID: ");
                    String questionId = scanner.nextLine();
                    System.out.print("Enter answer text: ");
                    String answerText = scanner.nextLine();
                    client.answerQuestion(questionId, answerText);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }
}
