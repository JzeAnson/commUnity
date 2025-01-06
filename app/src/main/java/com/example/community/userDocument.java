package com.example.community;

public class userDocument {

    private static userDocument instance;
    private String documentId;

    private userDocument() {}

    public static synchronized userDocument getInstance() {
        if (instance == null) {
            instance = new userDocument();
        }
        return instance;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}
