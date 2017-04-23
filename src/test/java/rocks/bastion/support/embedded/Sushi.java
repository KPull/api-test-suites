package rocks.bastion.support.embedded;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * A model object that represents a single Sushi object for the SushiService testing API.
 */
public class Sushi {

    public static Builder newSushi() {
        return new Builder();
    }

    private long id;
    private String name;
    private BigDecimal price;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Type type;

    protected Sushi() {
        // required for deserialization
    }

    private Sushi(Builder builder) {
        id = builder.id;
        name = builder.name;
        price = builder.price;
        type = builder.type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sushi sushi = (Sushi) o;

        if (getId() != sushi.getId()) return false;
        if (getName() != null ? !getName().equals(sushi.getName()) : sushi.getName() != null) return false;
        if (getPrice() != null ? !getPrice().equals(sushi.getPrice()) : sushi.getPrice() != null) return false;
        return getType() == sushi.getType();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    public enum Type {
        NIGIRI,
        SASHIMI,
        MAKI
    }

    public static final class Builder {

        private long id;
        private String name;
        private BigDecimal price;
        private Type type;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }


        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder price(BigDecimal val) {
            price = val;
            return this;
        }

        public Builder price(long val) {
            price = new BigDecimal(val);
            return this;
        }

        public Builder type(Type val) {
            type = val;
            return this;
        }

        public Sushi build() {
            return new Sushi(this);
        }
    }
}