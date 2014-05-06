public class VectorUtils
{
    public VectorUtils()
    {
    }

    public Vector3 Vector3Addition(Vector3 u, Vector3 v)
    {
        Vector3 w = new Vector3();

        w.X = u.X + v.X;
        w.Y = u.Y + v.Y;
        w.Z = u.Z + v.Z;

        return w;
    }

    public Vector3 Vector3Substraction(Vector3 u, Vector3 v)
    {
        Vector3 w = new Vector3();

        w.X = v.X - u.X;
        w.Y = v.Y - u.Y;
        w.Z = v.Z - u.Z;

        return w;
    }

    public Vector3 Vector3Multiplication(Vector3 u, float r)
    {
        Vector3 w = new Vector3();

        w.X = u.X * r;
        w.Y = u.Y * r;
        w.Z = u.Z * r;

        return w;
    }

    public Vector3 Vector3CrossProduct(Vector3 u, Vector3 v)
    {
        Vector3 w = new Vector3();

        w.X = u.Y * v.Z - u.Z * v.Y;
        w.Y = u.Z * v.X - u.X * v.Z;
        w.Z = u.X * v.Y - u.Y * v.X;

        return w;
    }

    public float Vector3DotProduct(Vector3 u, Vector3 v)
    {
        return (v.X * u.X) + (v.Y * u.Y) + (v.Z * u.Z);
    }

}
