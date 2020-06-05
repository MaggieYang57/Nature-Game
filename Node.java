public class Node {

    public Point position;
    public double f, g, h;
    public Node parent;

    public Node(Node parent, double g, double h, Point position) {
        this.parent = parent;
        this.position = position;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }
    
    public Node getParent()
    {
    	return parent;
    }

    public void setParent(Node p) {
        this.parent = p;
    }
    
    public Point getPosition(){return position;}


    public void setF(double value) {
        this.f = value;
    }
    
    public double getF()
    {
    	return this.f;
    }
    
    public double getG(){return g;}
    public double getH(){return h;}

    public void setG(double value) {
        this.g = value;
    }

    public void setH(double value) {
        this.h = value;
    }

    public boolean equals(Node other) {
        if (other == null) {
            return false;
        }
        return this.position.x == other.position.x && this.position.y == other.position.y;
    }

}

//public class Node {
//        public int g; //distance from start
//        public int h; //heursitc distance
//        public int f; //total distance f=g+h
//        public Node prev_node; // prior node;
//        public Point position;
//
//        public Node (int g, int h, int f, Point position, Node prev_node){
//            this.g = g; //no distance from the start
//            this.h = h; // heuristic distance from the gaol
//            this.f = f; //total distance f = g+h
//            this.prev_node = prev_node; //parent node;
//            this.position = position;
//            // this.currentPoint = currentPoint;
//        }
//
//        //used to map p -> node
//        public boolean containsPoint(Point p ){
//
//            if(this.position == p){
//                return true;
//            }
//            else{
//
//                return false;
//            }
//        }
//
//        //public int getG(){return g;}
//        public int getH(){return h;}
//        public int getF(){return f;}
//        public void setG(int g){this.g = g;}
//        public void setH(int h){this.h = h;}
//        public int getG(){return g;}
//        public void setPostion(Point p){position = p;}
//        public Point getPosition(){return position;}
//        public Node getPrevNode(){return prev_node;}
//        public String toString(){return "getX() = "+ this.position.x + " getY() = " + this.position.y; }
//
//    }