/**
 * Kelas yang merepresentasikan vektor tiga dimensi
 */
public class Vector3
{
    public float X;
    public float Y;
    public float Z;

    // konstruktor tanpa parameter
    public Vector3()
    {
        X = 0.0f;
        Y = 0.0f;
        Z = 0.0f;
    }

    // konstryktor dengan parameter X, Y, Z
    public Vector3(float X, float Y, float Z)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    // mendapatkan invers dari vektor
    public Vector3 getInverse()
    {
        return new Vector3(-X, -Y, -Z);
    }

    // mendapatkan panjang vektor
    public float getLength()
    {
        return (float)Math.sqrt((X * X) + (Y * Y) + (Z * Z));
    }

    // mendapatkan vektor normal
    public Vector3 getNormal()
    {
        float l = getLength();

        if(l == 0.0f)
            return null;

        return new Vector3(X / l, Y / l, Z / l);
    }
}
