package bllm;



public final class BitcoinPriceObject {
    public final String status;
    public final String name;
    public final String unit;
    public final String period;
    public final String description;
    public final Value values[];

    public BitcoinPriceObject(String status, String name, String unit, String period, String description, Value[] values){
        this.status = status;
        this.name = name;
        this.unit = unit;
        this.period = period;
        this.description = description;
        this.values = values;
    }

    public static final class Value {
        public final long x;
        public final double y;

        public Value(long x, double y){
            this.x = x;
            this.y = y;
        }
    }

  public double getCurrentValue(){
    return this.values[this.values.length-1].y;   
  }
}
