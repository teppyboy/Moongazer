package org.vibecoders.moongazer.ui.storymodeUI;

public class StageData {
    private int stageId;
    private String name;
    private String info;
//    private String[] challenges;
//    private String[] rewards;
    private String map;

    public StageData(int id, String name, String info, String map) {
        this.stageId = id;
        this.name = name;
        this.info = info;
//        this.challenges = challenges;
//        this.rewards = rewards;
        this.map = map;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String[] getRewards() {
//        return rewards;
//    }
//
//    public void setRewards(String[] rewards) {
//        this.rewards = rewards;
//    }
//
//    public String[] getChallenges() {
//        return challenges;
//    }
//
//    public void setChallenges(String[] challenges) {
//        this.challenges = challenges;
//    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
