package net.dlyt.qyds.common.dto.ext;

import net.dlyt.qyds.common.dto.ErpStore;

/**
 * Created by zlh on 2016/7/28.
 */
public class ErpStoreExt extends ErpStore {

    // 更新方式,10新增,20修改,30删除
    private String style;

    private  String sku;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
