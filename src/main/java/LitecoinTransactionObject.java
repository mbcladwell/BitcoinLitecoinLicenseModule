package llm;

/*
 *   https://timboudreau.com/blog/json/read
 *
 */

public final class LitecoinTransactionObject {
  public final long block_height;
  public final long block_index;
  public final String hash;
  public final String[] addresses;
  public final long total; // number of Satoshis
  public final long fees;
  public final long size;
  public final String preference;
  public final String relayed_by;
  public final String received; // date of transaction
  public final long ver;
  public final boolean double_spend;
  public final long vin_sz;
  public final long vout_sz;
  public final long confirmations;
  public final Input inputs[];
  public final Output outputs[];

  public LitecoinTransactionObject(
      long block_height,
      long block_index,
      String hash,
      String[] addresses,
      long total,
      long fees,
      long size,
      String preference,
      String relayed_by,
      String received,
      long ver,
      boolean double_spend,
      long vin_sz,
      long vout_sz,
      long confirmations,
      Input[] inputs,
      Output[] outputs) {
    this.block_height = block_height;
    this.block_index = block_index;
    this.hash = hash;
    this.addresses = addresses;
    this.total = total;
    this.fees = fees;
    this.size = size;
    this.preference = preference;
    this.relayed_by = relayed_by;
    this.received = received;
    this.ver = ver;
    this.double_spend = double_spend;
    this.vin_sz = vin_sz;
    this.vout_sz = vout_sz;
    this.confirmations = confirmations;
    this.inputs = inputs;
    this.outputs = outputs;
  }

  public static final class Input {
    public final String prev_hash;
    public final long output_index;
    public final String script;
    public final long output_value;
    public final long sequence;
    public final String[] addresses;
    public final String script_type;
    public final long age;

    public Input(
        String prev_hash,
        long output_index,
        String script,
        long output_value,
        long sequence,
        String[] addresses,
        String script_type,
        long age) {
      this.prev_hash = prev_hash;
      this.output_index = output_index;
      this.script = script;
      this.output_value = output_value;
      this.sequence = sequence;
      this.addresses = addresses;
      this.script_type = script_type;
      this.age = age;
    }
  }

  public static final class Output {
    public final long value;
    public final String script;
    public final String[] addresses;
    public final String script_type;

    public Output(long value, String script, String[] addresses, String script_type) {
      this.value = value;
      this.script = script;
      this.addresses = addresses;
      this.script_type = script_type;
    }

    public String[] getAddresses() {
      return this.addresses;
    }

    public long getValue() {
      return this.value;
    }
  }

  public String getTransactionDate() {
    return this.received;
  }

  public Output[] getOutputs() {
    return this.outputs;
  }

  public long getConfirmations() {
    return this.confirmations;
  }

  public boolean getDoubleSpend() {
    return this.double_spend;
  }
}
