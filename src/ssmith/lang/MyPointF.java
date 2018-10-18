package ssmith.lang;


public class MyPointF {
	
	public float x, y;
	
	public MyPointF() {
		this(0, 0);
	}
	
	
	public MyPointF(float _x, float _y) {
		super();
		
		this.x = _x;
		this.y = _y;
	}
	
	
	public String toString() {
		return x + "," + y;
	}
	
	
	public MyPointF subtract(MyPointF o) {
		MyPointF newone = new MyPointF(this.x, this.y);
		
		newone.x -= o.x;
		newone.y -= o.y;
		
		return newone;
	}

	
	public MyPointF multiply(float o) {
		MyPointF newone = new MyPointF(this.x, this.y);
		
		newone.x *= o;
		newone.y *= o;
		
		return newone;
	}

	
	public MyPointF multiplyLocal(float o) {
		x *= o;
		y *= o;
		
		return this;
	}

	
	public MyPointF notFactorial(float o) {
		MyPointF newone = new MyPointF(this.x, this.y);
		
		newone.x = o / newone.x;
		newone.y = o / newone.y;
		
		return newone;
	}

	
	public MyPointF subtract(float x, float y) {
		MyPointF newone = new MyPointF(this.x, this.y);
		
		newone.x -= x;
		newone.y -= y;
		
		return newone;
	}

	
	public MyPointF subtractLocal(MyPointF o) {
		return this.subtractLocal(o.x, o.y);
	}
	
	
	public MyPointF subtractLocal(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	
	public MyPointF add(MyPointF other) {
		MyPointF pf = new MyPointF(x, y);
		pf.x = pf.x + other.x;
		pf.y = pf.y + other.y;
		return pf;
	}

	
	public MyPointF averageLocal(MyPointF other) {
		this.x = (this.x + other.x)/2;
		this.y = (this.y + other.y)/2;
		return this;
	}

	
	public MyPointF normalizeLocal() {
		float len = this.length();
		x = x / len;
		y = y / len;
		return this;
	}

	
	public MyPointF normalize() {
		float len = this.length();
		float x2 = x / len;
		float y2 = y / len;
		return new MyPointF(x2, y2);
	}
	
	
	public float distance(MyPointF t) {
		return GeometryFuncs.distance(this.x, this.y, t.x, t.y);
	}
	
	
	public MyPointF copy() {
		return this.multiply(1);
	}
	
	
	public float length() {
		return GeometryFuncs.distance(0, 0, this.x, this.y);
	}

	
}
