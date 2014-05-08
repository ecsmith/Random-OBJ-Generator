import javax.vecmath.Vector3f;


public class Vector3 extends Vector3f
{
    public float X;
    public float Y;
    public float Z;

    public Vector3()
    {
        X = 0.0f;
        Y = 0.0f;
        Z = 0.0f;
    }

    public Vector3(float X, float Y, float Z)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public Vector3 getInverse()
    {
        return new Vector3(-X, -Y, -Z);
    }

    public float getLength()
    {
        return (float)Math.sqrt((X * X) + (Y * Y) + (Z * Z));
    }

    public Vector3 getNormal()
    {
        float l = getLength();

        if(l == 0.0f)
            return null;

        return new Vector3(X / l, Y / l, Z / l);
    }
}
