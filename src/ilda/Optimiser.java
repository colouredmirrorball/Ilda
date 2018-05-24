package ilda;

import java.util.ArrayList;

/**
 * Optimises a frame or frame segments according to its OptimisationSettings.
 */
public class Optimiser {


    private OptimisationSettings settings;

    public Optimiser(OptimisationSettings settings) {
        this.settings = settings;
    }

    public OptimisationSettings getSettings() {
        return settings;
    }

    public void setSettings(OptimisationSettings settings) {
        this.settings = settings;
    }

    public ArrayList<IldaPoint> optimiseSegment(ArrayList<IldaPoint> points)
    {
        float maxdistsqb = settings.maxDistBlank * settings.maxDistBlank;
        float maxdistsql = settings.maxDistLit * settings.maxDistLit;
        for (int i = points.size() - 2; i >= 0; i--) {
            IldaPoint prevp = points.get(i + 1);
            IldaPoint p = points.get(i);
            IldaPoint nextp = null;
            if (i != 0) nextp = points.get(i - 1);

            float dpsq = (prevp.x - p.x) * (prevp.x - p.x) + (prevp.y - p.y) * (prevp.y - p.y) + (prevp.z - p.z) * (prevp.z - p.z);
            float dnsq = 0;
            if (nextp != null)
                dnsq = (nextp.x - p.x) * (nextp.x - p.x) + (nextp.y - p.y) * (nextp.y - p.y) + (nextp.z - p.z) * (nextp.z - p.z);

            if (settings.isAngleDwell()) {
                if (nextp != null) {
                    float dpnsq = (nextp.x - prevp.x) * (nextp.x - prevp.x) + (nextp.y - prevp.y) * (nextp.y - prevp.y) + (nextp.z - prevp.z) * (nextp.z - prevp.z);
                    float factor = (dpsq + dnsq) / dpnsq;
                    int dwellPoints = (int) (settings.angleDwellFactor * factor);
                    //ilda.parent.println(factor, dwellPoints);
                }
            }

            if (settings.interpolateBlanked || settings.interpolateLit) {


                if ((prevp.blanked && dpsq > maxdistsqb && settings.interpolateBlanked) || (!prevp.blanked && dpsq > maxdistsql && settings.interpolateLit)) {
                    double dist = Math.sqrt(dpsq);
                    double maxDist = prevp.blanked ? settings.maxDistBlank : settings.maxDistLit;
                    int addedPoints = (int) (dist / maxDist);

                    for (int j = 0; j <= addedPoints; j++) {
                        IldaPoint newp = new IldaPoint(prevp);
                        float factor = (float) ((1 - (dist - j * maxDist) / dist));
                        newp.x = prevp.x + (p.x - prevp.x) * factor;
                        newp.y = prevp.y + (p.y - prevp.y) * factor;
                        newp.z = prevp.z + (p.z - prevp.z) * factor;
                        points.add(i + 1, newp);
                    }
                }

            }


        }


        return points;
    }


}
