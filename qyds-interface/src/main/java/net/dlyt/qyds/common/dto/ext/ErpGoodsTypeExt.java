package net.dlyt.qyds.common.dto.ext;

import net.dlyt.qyds.common.dto.ErpGoodsType;

/**
 * Created by zlh on 2016/7/28.
 */
public class ErpGoodsTypeExt extends ErpGoodsType {

    // 更新方式,10新增,20修改,30删除
    private String style;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}