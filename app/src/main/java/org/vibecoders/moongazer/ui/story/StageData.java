package org.vibecoders.moongazer.ui.story;

public class StageData {
    private int stageId;
    private String name;
    private String info;
    private int bestScore;
    private String map;

    /**
     * Constructs a stage data object with basic information.
     *
     * @param id the stage ID
     * @param name the stage name
     * @param info the stage description
     * @param map the map texture path
     */
    public StageData(int id, String name, String info, String map) {
        this.stageId = id;
        this.name = name;
        this.info = info;
        this.map = map;
    }

    /**
     * Constructs a stage data object with best score.
     *
     * @param id the stage ID
     * @param name the stage name
     * @param info the stage description
     * @param map the map texture path
     * @param bestScore the best score for this stage
     */
    public StageData(int id, String name, String info, String map, int bestScore) {
        this.stageId = id;
        this.name = name;
        this.info = info;
        this.map = map;
        this.bestScore = bestScore;
    }

    /**
     * Gets the stage ID.
     *
     * @return the stage ID
     */
    public int getStageId() {
        return stageId;
    }

    /**
     * Sets the stage ID.
     *
     * @param stageId the stage ID to set
     */
    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    /**
     * Gets the stage name.
     *
     * @return the stage name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the stage name.
     *
     * @param name the stage name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the best score for this stage.
     *
     * @return the best score
     */
    public int getScore() {
        return bestScore;
    }

    /**
     * Gets the map texture path.
     *
     * @return the map texture path
     */
    public String getMap() {
        return map;
    }

    /**
     * Sets the map texture path.
     *
     * @param map the map texture path to set
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Gets the stage description.
     *
     * @return the stage description
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the stage description.
     *
     * @param info the stage description to set
     */
    public void setInfo(String info) {
        this.info = info;
    }
}
