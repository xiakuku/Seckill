package cn.itcast.pinyougou.pojo;

import java.io.Serializable;

public class OrderRecord implements Serializable {



    private String userId;

    private Long id;

    public OrderRecord(String userId, Long id) {
        this.userId = userId;
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
