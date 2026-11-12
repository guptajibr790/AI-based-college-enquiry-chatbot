package com.chatbot.model;

public class Faq {
    private int id;
    private String question;
    private String keywords;
    private String answer;

    public Faq() { }

    public Faq(int id, String question, String keywords, String answer) {
        this.id = id;
        this.question = question;
        this.keywords = keywords;
        this.answer = answer;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
