package ilda;

import java.util.List;

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

    public List<IldaPoint> optimiseSegment(List<IldaPoint> points) {
        float maxdistsqb = settings.maxDistBlank * settings.maxDistBlank;
        float maxdistsql = settings.maxDistLit * settings.maxDistLit;
        for (int i = points.size() - 2; i >= 0; i--) {
            IldaPoint prevp = points.get(i + 1);
            IldaPoint p = points.get(i);
            IldaPoint nextp = null;
            if (i != 0)
                nextp = points.get(i - 1);

            float dpsq = (prevp.getX() - p.getX()) * (prevp.getX() - p.getX()) + (prevp.getY() - p
                .getY()) * (prevp.getY() - p.getY()) + (prevp.getZ() - p.getZ()) * (prevp.getZ() - p
                .getZ());
            float dnsq = 0;
            if (nextp != null)
                dnsq = (nextp.getX() - p.getX()) * (nextp.getX() - p.getX()) + (nextp.getY() - p
                    .getY()) * (nextp.getY() - p.getY()) + (nextp.getZ() - p.getZ()) * (nextp
                    .getZ() - p.getZ());

            if (settings.isAngleDwell() && nextp != null) {
                float dpnsq = (nextp.getX() - prevp.getX()) * (nextp.getX() - prevp.getX()) + (nextp
                    .getY() - prevp.getY()) * (nextp.getY() - prevp.getY()) + (nextp.getZ() - prevp
                    .getZ()) * (nextp.getZ() - prevp.getZ());
                float factor = (dpsq + dnsq) / dpnsq;
                int dwellPoints = (int) (settings.angleDwellFactor * factor);
                //ilda.parent.println(factor, dwellPoints);
            }

            if (settings.interpolateBlanked || settings.interpolateLit) {
                if ((prevp.blanked && dpsq > maxdistsqb && settings.interpolateBlanked) || (!prevp.blanked && dpsq > maxdistsql && settings.interpolateLit)) {
                    double dist = Math.sqrt(dpsq);
                    double maxDist = prevp.blanked ? settings.maxDistBlank : settings.maxDistLit;
                    int addedPoints = (int) (dist / maxDist);

                    for (int j = 0; j <= addedPoints; j++) {
                        IldaPoint newp = new IldaPoint(prevp);
                        float factor = (float) (1 - (dist - j * maxDist) / dist);
                        newp.setPosition(prevp.getX() + (p.getX() - prevp.getX()) * factor,
                            prevp.getY() + (p.getY() - prevp.getY()) * factor,
                            prevp.getZ() + (p.getZ() - prevp.getZ()) * factor);
                        points.add(i + 1, newp);
                    }
                }
            }
        }
        return points;
    }


}
