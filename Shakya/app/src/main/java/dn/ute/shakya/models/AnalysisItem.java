package dn.ute.shakya.models;

import android.support.annotation.NonNull;

public class AnalysisItem implements Comparable<AnalysisItem>{
    private long id;
    private String content = "";
    private long numberOfTimeIsCorrect = 0;
    private long numberOfTimeIsWrong = 0;

    public AnalysisItem() {

    }

    public AnalysisItem(long id, String content) {
        this.id = id;
        this.content = content;
        numberOfTimeIsCorrect = 0;
        numberOfTimeIsWrong = 0;
    }

    public AnalysisItem(long id, String content, long numberOfTimeIsCorrect, long numberOfTimeIsWrong) {
        this.id = id;
        this.content = content;
        this.numberOfTimeIsCorrect = numberOfTimeIsCorrect;
        this.numberOfTimeIsWrong = numberOfTimeIsWrong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getNumberOfTimeIsCorrect() {
        return numberOfTimeIsCorrect;
    }

    public void setNumberOfTimeIsCorrect(long numberOfTimeIsCorrect) {
        this.numberOfTimeIsCorrect = numberOfTimeIsCorrect;
    }

    public long getNumberOfTimeIsWrong() {
        return numberOfTimeIsWrong;
    }

    public void setNumberOfTimeIsWrong(boolean isWrong) {
        if(!isWrong && numberOfTimeIsWrong > 0) numberOfTimeIsWrong -= 1;
        if(isWrong && numberOfTimeIsWrong < 10) numberOfTimeIsWrong += 1;
    }

    public float getWrongRate(){
        return ((float) numberOfTimeIsWrong/10)*100;
    }

    @Override
    public int compareTo(@NonNull AnalysisItem analysisItem) {
        return (int)(analysisItem.getWrongRate() - this.getWrongRate());
    }
}
