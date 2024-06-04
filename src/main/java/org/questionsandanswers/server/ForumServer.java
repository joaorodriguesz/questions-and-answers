package org.questionsandanswers.server;

import forum.ForumGrpc;
import forum.ForumOuterClass;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.*;


public class ForumServer {
    private final int port;
    private final Server server;
    private final Map<String, ForumOuterClass.Question> questions = new HashMap<>();

    public ForumServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new ForumImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ForumServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private class ForumImpl extends ForumGrpc.ForumImplBase {
        @Override
        public void listQuestions(ForumOuterClass.Empty request, StreamObserver<ForumOuterClass.QuestionList> responseObserver) {
            ForumOuterClass.QuestionList.Builder questionListBuilder = ForumOuterClass.QuestionList.newBuilder();
            for (ForumOuterClass.Question question : questions.values()) {
                questionListBuilder.addQuestions(question);
            }
            responseObserver.onNext(questionListBuilder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void addQuestion(ForumOuterClass.Question request, StreamObserver<ForumOuterClass.Empty> responseObserver) {
            String questionId = UUID.randomUUID().toString();
            questions.put(questionId, request.toBuilder().setId(questionId).build());
            System.out.println("Added question with ID: " + questionId);
            responseObserver.onNext(ForumOuterClass.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void answerQuestion(ForumOuterClass.AnswerRequest request, StreamObserver<ForumOuterClass.Empty> responseObserver) {
            String questionId = request.getQuestionId();
            String answerText = request.getAnswerText();

            ForumOuterClass.Question question = questions.get(questionId);
            if (question != null) {
                question = question.toBuilder().addAnswers(answerText).build();
                questions.put(questionId, question);
                System.out.println("Answered question with ID: " + questionId);
            } else {
                System.out.println("Question with ID " + questionId + " not found.");
            }
            responseObserver.onNext(ForumOuterClass.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ForumServer server = new ForumServer(8080);
        server.start();
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
