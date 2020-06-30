package mongo.dao.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OptionDTO<Id, Name> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Id id;
    private Name name;

    public OptionDTO() {
        super();
    }

    public OptionDTO(Id id, Name name) {
        super();
        this.id = id;
        this.name = name;
    }

}
