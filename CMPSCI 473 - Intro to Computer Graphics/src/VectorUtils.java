 class VectorUtils
{
    public VectorUtils()
    {
    }

    public CustomVector3f Vector3Addition(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.X + v.X;
        w.Y = u.Y + v.Y;
        w.Z = u.Z + v.Z;

        return w;
    }

    public CustomVector3f Vector3Substraction(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = v.X - u.X;
        w.Y = v.Y - u.Y;
        w.Z = v.Z - u.Z;

        return w;
    }

    public CustomVector3f Vector3Multiplication(CustomVector3f u, float r)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.X * r;
        w.Y = u.Y * r;
        w.Z = u.Z * r;

        return w;
    }

    public CustomVector3f Vector3CrossProduct(CustomVector3f u, CustomVector3f v)
    {
        CustomVector3f w = new CustomVector3f();

        w.X = u.Y * v.Z - u.Z * v.Y;
        w.Y = u.Z * v.X - u.X * v.Z;
        w.Z = u.X * v.Y - u.Y * v.X;

        return w;
    }

    public float Vector3DotProduct(CustomVector3f u, CustomVector3f v)
    {
        return (v.X * u.X) + (v.Y * u.Y) + (v.Z * u.Z);
    }

}
