package edu.dartmouth.cs.myrun5.model;

public class
ManualEntry {
    private String header;
    private String content;

    public ManualEntry(String header, String content){
        this.header = header;
        this.content = content;
    }

    public void setHeader(String header){
        this.header = header;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }
}
