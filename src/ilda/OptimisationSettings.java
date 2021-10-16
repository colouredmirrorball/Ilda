package ilda;

import processing.data.FloatList;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Bundles all optimisation settings, so they are easily shared across the package and easily transported to other
 * projects. You can create a settings file for each projector and load it in all your sketches and projects without
 * having to recreate the settings again.
 */
public class OptimisationSettings
{


    boolean interpolateLit;
    boolean interpolateBlanked;
    float maxDistLit = 0.03f;
    float maxDistBlank = 0.03f;

    boolean angleDwell = true;
    float angleDwellFactor = 1;

    boolean blankDwell = true;
    int blankDwellAmount = 2;

    boolean clippingEnabled = false;
    boolean reduceData = true;
    float[] clipBounds = new float[]{-1f, 1f, 1f, -1f};

    public OptimisationSettings()
    {
        interpolateLit = true;
        interpolateBlanked = true;
    }

    public float getAngleDwellFactor()
    {
        return angleDwellFactor;
    }

    public OptimisationSettings setAngleDwellFactor(float angleDwellFactor)
    {
        this.angleDwellFactor = angleDwellFactor;
        return this;
    }

    public boolean isBlankDwell()
    {
        return blankDwell;
    }

    public OptimisationSettings setBlankDwell(boolean blankDwell)
    {
        this.blankDwell = blankDwell;
        return this;
    }

    public int getBlankDwellAmount()
    {
        return blankDwellAmount;
    }

    public OptimisationSettings setBlankDwellAmount(int blankDwellAmount)
    {
        this.blankDwellAmount = blankDwellAmount;
        return this;
    }

    public float[] getClipBounds()
    {
        return clipBounds;
    }

    public OptimisationSettings setClipBounds(float[] clipBounds)
    {
        this.clipBounds = clipBounds;
        return this;
    }

    public boolean isInterpolateLit()
    {
        return interpolateLit;
    }

    public OptimisationSettings setInterpolateLit(boolean interpolateLit)
    {
        this.interpolateLit = interpolateLit;
        return this;
    }

    public boolean isInterpolateBlanked()
    {
        return interpolateBlanked;
    }

    public OptimisationSettings setInterpolateBlanked(boolean interpolateBlanked)
    {
        this.interpolateBlanked = interpolateBlanked;
        return this;
    }

    public float getMaxDistLit()
    {
        return maxDistLit;
    }

    public OptimisationSettings setMaxDistLit(float maxDistLit)
    {
        this.maxDistLit = maxDistLit;
        return this;
    }

    public float getMaxDistBlank()
    {
        return maxDistBlank;
    }

    public OptimisationSettings setMaxDistBlank(float maxDistBlank)
    {
        this.maxDistBlank = maxDistBlank;
        return this;
    }

    public boolean isAngleDwell()
    {
        return angleDwell;
    }

    public OptimisationSettings setAngleDwell(boolean angleDwell)
    {
        this.angleDwell = angleDwell;
        return this;
    }

    public boolean isClippingEnabled()
    {
        return clippingEnabled;
    }

    public OptimisationSettings setClippingEnabled(boolean clippingEnabled)
    {
        this.clippingEnabled = clippingEnabled;
        return this;
    }

    public boolean isReduceData()
    {
        return reduceData;
    }

    public OptimisationSettings setReduceData(boolean reduceData)
    {
        this.reduceData = reduceData;
        return this;
    }


    public String toJSON()
    {
        JSONObject output = new JSONObject();
        output.setFloat("maxDistBlank", maxDistBlank);
        output.setFloat("maxDistLit", maxDistLit);
        output.setBoolean("isAngleDwell", angleDwell);
        output.setBoolean("isInterpolateBlanked", interpolateBlanked);
        output.setBoolean("isInterpolateLit", interpolateLit);
        output.setInt("blankDwellAmount", blankDwellAmount);
        output.setFloat("angleDwellFactor", angleDwellFactor);
        output.setBoolean("reduceData", reduceData);
        output.setBoolean("clippingEnabled", clippingEnabled);
        output.setJSONArray("clipBounds", new JSONArray(new FloatList(clipBounds)));

        return output.toString();
    }

    public void fromJSON(String json)
    {
        JSONObject input = JSONObject.parse(json);
        maxDistBlank = input.getFloat("maxDistBlank", maxDistBlank);
        maxDistLit = input.getFloat("maxDistLit", maxDistLit);
        angleDwell = input.getBoolean("isAngleDwell", angleDwell);
        interpolateBlanked = input.getBoolean("isInterpolateBlanked", interpolateBlanked);
        interpolateLit = input.getBoolean("isInterpolateLit", interpolateLit);
        blankDwellAmount = input.getInt("blankDwellAmount", blankDwellAmount);
        angleDwellFactor = input.getFloat("angleDwellFactor", angleDwellFactor);
        reduceData = input.getBoolean("reduceData", reduceData);
        clippingEnabled = input.getBoolean("clippingEnabled", clippingEnabled);
        clipBounds = input.getJSONArray("clipBounds").getFloatArray();
    }




}
