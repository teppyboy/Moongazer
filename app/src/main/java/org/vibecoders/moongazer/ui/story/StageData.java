package org.vibecoders.moongazer.ui.story;

public class StageData {
    private int stageId;
    private String name;
    private String info;
    private int bestScore;
    private String map;

    public StageData(int id, String name, String info, String map) {
        this.stageId = id;
        this.name = name;
        this.info = info;
        this.map = map;
    }

    public StageData(int id, String name, String info, String map, int bestScore) {
        this.stageId = id;
        this.name = name;
        this.info = info;
        this.map = map;
        this.bestScore = bestScore;
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

    public int getScore() {
        return bestScore;
    }

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
