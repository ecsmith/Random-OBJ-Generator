// Triangle class
// defines a Triangle shape

import javax.vecmath.*;

class Triangle extends Shape {
	private Vector3f p0, p1, p2;	// three vertices make a triangle
	private Vector3f n0, n1, n2;	// normal at each vertex

	public Triangle() {
	}
	public Triangle(Vector3f _p0, Vector3f _p1, Vector3f _p2, Material mat) {
		p0 = new Vector3f(_p0);
		p1 = new Vector3f(_p1);
		p2 = new Vector3f(_p2);
		material = mat;
		Vector3f normal = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		v1.sub(p1, p0);
		v2.sub(p2, p0);
		normal.cross(v1, v2);
		normal.normalize();				// compute default normal:
		n0 = new Vector3f(normal);		// the normal of the plane defined by the triangle
		n1 = new Vector3f(normal);
		n2 = new Vector3f(normal);
	}
	public Triangle(Vector3f _p0, Vector3f _p1, Vector3f _p2,
					Vector3f _n0, Vector3f _n1, Vector3f _n2,
					Material mat) {
		p0 = new Vector3f(_p0);
		p1 = new Vector3f(_p1);
		p2 = new Vector3f(_p2);
		material = mat;
		n0 = new Vector3f(_n0);		// the normal of the plane defined by the triangle
		n1 = new Vector3f(_n1);
		n2 = new Vector3f(_n2);
	}
	public HitRecord hit(Ray ray, float tmin, float tmax) {

		/* YOUR WORK HERE: complete the triangle's intersection routine
		 * Normal should be computed by a bilinear interpolation from n0, n1 and n2
		 * using the barycentric coordinates: alpha, beta, (1.0 - alpha - beta) */
		/* compute ray-plane intersection */
		//NOT WORKING
//		float t = 0.0f; float a0 = 0.0f; float a1 = 0.0f; float a2 = 0.0f;  
//		
//		Vector3f vec0 = new Vector3f();Vector3f vec1 = new Vector3f();
//		Vector3f vec2 = new Vector3f();	
//		
//		vec0.sub(p1,p0);
//		vec1.sub(p2,p1);
//		vec2.sub(ray.o,p0);
//		Matrix3f mat = new Matrix3f();
//		mat.setColumn(0, vec0);
//		mat.setColumn(1, vec1);
//		mat.setColumn(2, vec2);
//		float d = mat.determinant();
//		
//		if((0 <= a0) &&( 0 <= a1) && (0 <= a2) && ...){
//
//		HitRecord rec = new HitRecord();
//		rec.setShape(this);
//		rec.ray_intersects_something = true;
//		rec.pos = ray.pointAt(t);		// position of hit point
//		rec.t = t;						// parameter t (distance along the ray)
//		rec.material = material;		// material
//		rec.normal =calc_normal(rec.pos,center);					// normal at the hit point
//		rec.normal.normalize();			// normal should be normalized
//		}
//	
		return null;
	}
}
