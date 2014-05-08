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
    public static CustomVector3f Vector3Addition(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.X + v.X;
        w.Y = u.Y + v.Y;
        w.Z = u.Z + v.Z;

        return w;
    }

    public static CustomVector3f Vector3Substraction(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = v.X - u.X;
        w.Y = v.Y - u.Y;
        w.Z = v.Z - u.Z;

        return w;
    }

    public static CustomVector3f Vector3Multiplication(CustomVector3f u, float r)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.X * r;
        w.Y = u.Y * r;
        w.Z = u.Z * r;

        return w;
    }

    public static CustomVector3f Vector3CrossProduct(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.Y * v.Z - u.Z * v.Y;
        w.Y = u.Z * v.X - u.X * v.Z;
        w.Z = u.X * v.Y - u.Y * v.X;

        return w;
    }

    public static float Vector3DotProduct(CustomVector3f u, CustomVector3f v)
    {
        return (v.X * u.X) + (v.Y * u.Y) + (v.Z * u.Z);
    }
}
