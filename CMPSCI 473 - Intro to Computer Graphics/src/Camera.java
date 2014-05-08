

public class Camera
{
    CustomVector3f origViewDir = new CustomVector3f(0.0f, -70.0f, -150.0f);
    CustomVector3f origRightVector = new CustomVector3f(1.0f, 0.0f, 0.0f);
    CustomVector3f origUpVector = new CustomVector3f(0.0f, 1.0f, 0.0f);
    CustomVector3f origPosition = new CustomVector3f(75.5f, 90.0f, 150.0f);

    CustomVector3f viewDir;   
    CustomVector3f rightVector;    
    CustomVector3f upVector;       
    CustomVector3f position;   

    float rotatedX; 
    float rotatedY; 
    float rotatedZ;

    float PiDiv180 = (float)Math.PI / 180.0f;   

    VectorUtils vectorUtils = new VectorUtils();   

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


    public void move(CustomVector3f direction)
    {
        position = vectorUtils.Vector3Addition(position, direction);
    }

   
    public void rotateX(float angle)
    {
        rotatedX += angle;

        CustomVector3f temp1 = vectorUtils.Vector3Multiplication(viewDir, (float)Math.cos(angle * PiDiv180));
        CustomVector3f temp2 = vectorUtils.Vector3Multiplication(upVector, (float)Math.sin(angle * PiDiv180));

        viewDir = vectorUtils.Vector3Addition(temp1, temp2).getNormal();
        upVector = vectorUtils.Vector3Multiplication(vectorUtils.Vector3CrossProduct(viewDir, rightVector), -1.0f);
    }

 
    public void rotateY(float angle)
    {
        rotatedY += angle;

        CustomVector3f temp1 = vectorUtils.Vector3Multiplication(viewDir, (float)Math.cos(angle * PiDiv180));
        CustomVector3f temp2 = vectorUtils.Vector3Multiplication(rightVector, (float)Math.sin(angle * PiDiv180));

        viewDir = vectorUtils.Vector3Substraction(temp2, temp1);
        viewDir = viewDir.getNormal();
        rightVector = vectorUtils.Vector3CrossProduct(viewDir, upVector);
    }

  
    public void rotateZ(float angle)
    {
        rotatedZ += angle;

        CustomVector3f temp1 = vectorUtils.Vector3Multiplication(rightVector, (float)Math.cos(angle * PiDiv180));
        CustomVector3f temp2 = vectorUtils.Vector3Multiplication(upVector, (float)Math.sin(angle * PiDiv180));

        rightVector = vectorUtils.Vector3Addition(temp1, temp2).getNormal();
        upVector = vectorUtils.Vector3Multiplication(vectorUtils.Vector3CrossProduct(viewDir, rightVector), -1.0f);
    }

    
    public void moveForward(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(viewDir, -distance));
    }

   
    public void strafeRight(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(rightVector, distance));
    }

    
    public void moveUpward(float distance)
    {
        position = vectorUtils.Vector3Addition(position, vectorUtils.Vector3Multiplication(upVector, distance));
    }

    
    public CustomVector3f getCameraPosition()
    {
        return position;
    }

   
    public CustomVector3f getCameraTarget()
    {
        return vectorUtils.Vector3Addition(position, viewDir);
    }

   
    public CustomVector3f getUpVector()
    {
        return upVector;
    }
}
