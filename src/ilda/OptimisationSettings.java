package ilda;

/**
 * Bundles all optimisation settings so they are easily shared across the package and easily transported to other projects.
 * You can create a settings file for each projector and load it in all your sketches and projects without having to recreate the settings again.
 */
public class OptimisationSettings {


    protected boolean interpolateLit;
    protected boolean interpolateBlanked;
    protected float maxDistLit = 0.03f;
    protected float maxDistBlank = 0.03f;

    protected boolean angleDwell = true;
    protected float angleDwellFactor = 1;

    protected boolean blankDwell = true;
    protected int blankDwellAmount = 2;

    public OptimisationSettings() {
        interpolateLit = true;
        interpolateBlanked = true;
    }

    public boolean isInterpolateLit() {
        return interpolateLit;
    }

    public void setInterpolateLit(boolean interpolateLit) {
        this.interpolateLit = interpolateLit;
    }

    public boolean isInterpolateBlanked() {
        return interpolateBlanked;
    }

    public void setInterpolateBlanked(boolean interpolateBlanked) {
        this.interpolateBlanked = interpolateBlanked;
    }

    public float getMaxDistLit() {
        return maxDistLit;
    }

    public void setMaxDistLit(float maxDistLit) {
        this.maxDistLit = maxDistLit;
    }

    public float getMaxDistBlank() {
        return maxDistBlank;
    }

    public void setMaxDistBlank(float maxDistBlank) {
        this.maxDistBlank = maxDistBlank;
    }

    public boolean isAngleDwell() {
        return angleDwell;
    }

    public void setAngleDwell(boolean angleDwell) {
        this.angleDwell = angleDwell;
    }

    public String[] generateOptimisationFile()
    {
        String[] out = new String[8];
        out[0] = "interpolateLit " + interpolateLit;
        return out;
    }


}
