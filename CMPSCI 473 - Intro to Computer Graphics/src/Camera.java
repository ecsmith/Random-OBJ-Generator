
/**
 * Kelas yang mengatur pergerakan kamera
 * Perhitungan tidak menggunakan glRotate maupun glTranslate
 * tapi menggunakan perhitungan vektor
 */

public class Camera
{
    Vector3 origViewDir = new Vector3(0.0f, -70.0f, -150.0f);
    Vector3 origRightVector = new Vector3(1.0f, 0.0f, 0.0f);
    Vector3 origUpVector = new Vector3(0.0f, 1.0f, 0.0f);
    Vector3 origPosition = new Vector3(75.5f, 90.0f, 150.0f);

    Vector3 viewDir;   // arah pandangan kamera
    Vector3 rightVector;    // vektor kanan kamera
    Vector3 upVector;       // vektor atas kamera
    Vector3 position;   // posisi kamera

    float rotatedX;  // jumlah rotasi sumbu x
    float rotatedY;  // jumlah rotasi sumbu y
    float rotatedZ;  // jumlah rotasi sumbu z

    float PiDiv180 = (float)Math.PI / 180.0f;   // digunakan untuk mengubah derajat ke radian

    VectorUtils vectorUtils = new VectorUtils();    // untuk perhitungan vektor

    public Camera()
    {
        reset();
    }

    public void reset()
    {
        viewDir = origViewDir;
        rightVector = origRightVector;
        upVector = origUpVector;
        position = origPosition;
        viewDir = viewDir.getNormal();

        rotatedX = 0.0f;
        rotatedY = 0.0f;
        rotatedZ = 0.0f;
    }

    // perpindahan kamera berdasarkan arah
    public void move(Vector3 direction)
    {
        position = vectorUtils.Vector3Addition(position, direction);
    }

    // rotasi sumbu x
    public void rotateX(float angle)
    {
        rotatedX += angle;

        Vector3 temp1 = vectorUtils.Vector3Multiplication(viewDir, (float)Math.cos(angle * PiDiv180));
        Vector3 temp2 = vectorUtils.Vector3Multiplication(upVector, (float)Math.sin(angle * PiDiv180));

        viewDir = vectorUtils.Vector3Addition(temp1, temp2).getNormal();
        upVector = vectorUtils.Vector3Multiplication(vectorUtils.Vector3CrossProduct(viewDir, rightVector), -1.0f);
    }

    // rotasi sumbu y
    public void rotateY(float angle)
    {
        rotatedY += angle;

        Vector3 temp1 = vectorUtils.Vector3Multiplication(viewDir, (float)Math.cos(angle * PiDiv180));
        Vector3 temp2 = vectorUtils.Vector3Multiplication(rightVector, (float)Math.sin(angle * PiDiv180));

        viewDir = vectorUtils.Vector3Substraction(temp2, temp1);
        viewDir = viewDir.getNormal();
        rightVector = vectorUtils.Vector3CrossProduct(viewDir, upVector);
    }

    // rotasi sumbu z
    public void rotateZ(float angle)
    {
        rotatedZ += angle;

        Vector3 temp1 = vectorUtils.Vector3Multiplication(rightVector, (float)Math.cos(angle * PiDiv180));
        Vector3 temp2 = vectorUtils.Vector3Multiplication(upVector, (float)Math.sin(angle * PiDiv180));

        rightVector = vectorUtils.Vector3Addition(temp1, temp2).getNormal();
        upVector = vectorUtils.Vector3Multiplication(vectorUtils.Vector3CrossProduct(viewDir, rightVector), -1.0f);
    }

    // pergerakan maju atau mundur
    public void moveForward(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(viewDir, -distance));
    }

    // pergerakan ke kanan atau ke kiri
    public void strafeRight(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(rightVector, distance));
    }

    // pergerakan ke atas atau ke bawah
    public void moveUpward(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(upVector, distance));
    }

    // mendapatkan posisi kamera
    public Vector3 getCameraPosition()
    {
        return position;
    }

    // mendapatkan target kamera
    public Vector3 getCameraTarget()
    {
        return vectorUtils.Vector3Addition(position, viewDir);
    }

    // mendapatkan vektor atas kamera
    public Vector3 getUpVector()
    {
        return upVector;
    }


}
