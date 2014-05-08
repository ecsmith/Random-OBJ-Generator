import javax.vecmath.Vector3f;

public class CustomVector3f extends Vector3f
{
    public float X;
    public float Y;
    public float Z;

    public CustomVector3f()
    {
        X = 0.0f;
        Y = 0.0f;
        Z = 0.0f;
    }

    public CustomVector3f(float X, float Y, float Z)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public CustomVector3f getInverse()
    {
        return new CustomVector3f(-X, -Y, -Z);
    }

    public float getLength()
    {
        return (float)Math.sqrt((X * X) + (Y * Y) + (Z * Z));
    }

    public CustomVector3f getNormal()
    {
        float l = getLength();

        if(l == 0.0f)
            return null;

        return new CustomVector3f(X / l, Y / l, Z / l);
    }
}
