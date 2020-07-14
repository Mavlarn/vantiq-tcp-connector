package io.vantiq.ext.tcp.protocol;

public class PacketField {

    // {"field": "end", "fixCode": "21D3", "length": 2, "type": "byte"}
    String field;
    String fixCode;
    int length;
    String type;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFixCode() {
        return fixCode;
    }

    public void setFixCode(String fixCode) {
        this.fixCode = fixCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PacketField{" +
                "field='" + field + '\'' +
                ", fixCode='" + fixCode + '\'' +
                ", length=" + length +
                ", type='" + type + '\'' +
                '}';
    }
}
