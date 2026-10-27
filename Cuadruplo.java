public class Cuadruplo {
    public String op;
    public String arg1;
    public String arg2;
    public String res;

    public Cuadruplo(String op, String arg1, String arg2, String res) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.res = res;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-10s | %-10s | %-10s", 
                op, 
                arg1 == null ? "-" : arg1, 
                arg2 == null ? "-" : arg2, 
                res == null ? "-" : res);
    }
}