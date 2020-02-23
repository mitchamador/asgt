package gbas.gtbch.web.request;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class PensiUpdate implements Serializable {

    /**
     * тип update'а
     * "spr" - синхронизация справочников
     * "table" - обновление таблиц
     */
    String type;

    /**
     * список объектов для обновления
     */
    List<String> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
