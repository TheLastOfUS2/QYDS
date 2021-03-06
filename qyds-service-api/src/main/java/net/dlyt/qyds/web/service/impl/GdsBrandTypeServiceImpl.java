package net.dlyt.qyds.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import net.dlyt.qyds.common.dto.GdsBrand;
import net.dlyt.qyds.common.dto.GdsBrandType;
import net.dlyt.qyds.common.dto.GdsBrand;
import net.dlyt.qyds.common.dto.GdsBrandTypeDetail;
//import net.dlyt.qyds.common.dto.GdsTypeExt;
//import net.dlyt.qyds.common.dto.ext.CmsMasterExt;
import net.dlyt.qyds.dao.GdsBrandTypeMapper;
import net.dlyt.qyds.dao.GdsBrandTypeDetailMapper;
import net.dlyt.qyds.dao.ext.GdsBrandTypeMapperExt;
import net.dlyt.qyds.web.service.common.Constants;
import net.dlyt.qyds.web.service.GdsBrandTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by congkeyan on 16/7/20.
 */
@Service("gdsBrandTypeService")
@Transactional(readOnly = true)
public class GdsBrandTypeServiceImpl implements GdsBrandTypeService {


    @Autowired
    private GdsBrandTypeMapperExt gdsBrandTypeMapperExt;

    @Autowired
    private GdsBrandTypeMapper gdsBrandTypeMapper;

    @Autowired
    private GdsBrandTypeDetailMapper gdsBrandTypeDtailMapper;


    //获取分类列表
    public JSONObject getTreeList() {

        JSONObject json = new JSONObject();
        try {
            GdsBrandType gdsBrandType = new GdsBrandType();
            //调用service
            List<GdsBrandType> list = gdsBrandTypeMapperExt.getTreeList(gdsBrandType);
            //返回数据
            json.put("data", list);
            json.put("resultCode", Constants.NORMAL);
        } catch (Exception e) {
            json.put("resultCode", Constants.FAIL);
        }
        return json;
    }

    /**
     * 保存品牌系列主表 品牌系列详细
     *
     * @param gdsType
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public JSONObject save(GdsBrandType gdsType, GdsBrandTypeDetail gdsTypeDetail) {
        JSONObject map = new JSONObject();
        try {
            //店铺ID
            gdsType.setShopId(Constants.ORGID);
            if (gdsType == null) {
                map.put("resultCode", Constants.FAIL);
                return map;
            }

            if (StringUtils.isEmpty(gdsType.getGoodsTypeId())) {
                //新建
                //主表
                gdsType.setDeleted(Constants.DELETED_NO);
                String randomUUID = UUID.randomUUID().toString();
                gdsType.setGoodsTypeId(randomUUID);
                if (StringUtils.isEmpty(gdsType.getGoodsTypeIdParent())) {
                    gdsType.setGoodsTypeIdParent("0");
                }
                gdsType.setInsertTime(new Date());
                gdsType.setUpdateTime(new Date());

                //详细表
                gdsTypeDetail.setGoodsTypeId(randomUUID);
                gdsTypeDetail.setInsertTime(new Date());
                gdsTypeDetail.setUpdateTime(new Date());
                gdsTypeDetail.setDeleted(Constants.DELETED_NO);

                //插入主表还详细表
                gdsBrandTypeMapper.insertSelective(gdsType);
                gdsBrandTypeDtailMapper.insertSelective(gdsTypeDetail);

            } else {
                //编辑
                gdsType.setUpdateTime(new Date());
                gdsType.setInsertUserId(null);

                gdsTypeDetail.setUpdateTime(new Date());
                gdsTypeDetail.setInsertUserId(null);
                if (gdsTypeDetail.getImageUrl().length() == 0) {
                    gdsTypeDetail.setImageUrl(null);
                }
                //更新主表 详细表service
                gdsBrandTypeMapper.updateByPrimaryKeySelective(gdsType);
                gdsBrandTypeDtailMapper.updateByPrimaryKeySelective(gdsTypeDetail);

            }
            map.put("resultCode", Constants.NORMAL);
        } catch (Exception e) {
            map.put("resultCode", Constants.FAIL);
        }
        return map;
    }

    /**
     * 通过ID获取数据
     *
     * @param goodsTypeId
     * @return
     */
    public JSONObject edit(String goodsTypeId) {
        JSONObject json = new JSONObject();
        try {
            if (!StringUtils.isEmpty(goodsTypeId)) {
                //分类主表数据
                GdsBrandType gdstype = gdsBrandTypeMapper.selectByPrimaryKey(goodsTypeId);
                //分类详细的数据取得
                GdsBrandTypeDetail gdsTypeDetail = gdsBrandTypeDtailMapper.selectByPrimaryKey(goodsTypeId);
                //获取分类主标父ID的数据
                GdsBrandType gdstypeParent = gdsBrandTypeMapper.selectByPrimaryKey(gdstype.getGoodsTypeIdParent());

                JSONObject jsonObject = new JSONObject();
                //分类主表
                if (gdstype != null) {

                    jsonObject.put("goods_type_id", gdstype.getGoodsTypeId());
                    jsonObject.put("shop_id", gdstype.getShopId());
                    jsonObject.put("goods_type_id_parent", gdstype.getGoodsTypeIdParent());
                    jsonObject.put("type", gdstype.getType());
                    jsonObject.put("goods_type_code", gdstype.getGoodsTypeCode());
                    jsonObject.put("erp_style_no", gdstype.getErpStyleNo());
                    jsonObject.put("goods_type_name_en", gdstype.getGoodsTypeNameEn());
                    jsonObject.put("goods_type_name_cn", gdstype.getGoodsTypeNameCn());
                    jsonObject.put("goods_type_full_name_en", gdstype.getGoodsTypeFullNameEn());
                    jsonObject.put("goods_type_full_name_cn", gdstype.getGoodsTypeFullNameCn());
                }
                //分类详细
                if (gdsTypeDetail != null) {
                    jsonObject.put("image_url", gdsTypeDetail.getImageUrl());
                    jsonObject.put("introduce", gdsTypeDetail.getIntroduce());
                    jsonObject.put("property_define_json", gdsTypeDetail.getPropertyDefineJson());
                }
                //获取分类主标父ID的数据
                if (gdstypeParent != null) {
                    json.put("pName", gdstypeParent.getGoodsTypeNameCn());
                } else {
                    json.put("pName", "顶级分类");
                }

                json.put("data", jsonObject);
            }
            json.put("resultCode", Constants.NORMAL);
        } catch (Exception e) {
            json.put("resultCode", Constants.FAIL);
        }
        return json;
    }

    //删除分类节点
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public JSONObject delete(String goodsTypeId) {
        JSONObject map = new JSONObject();
        if (!StringUtils.isEmpty(goodsTypeId)) {
            //主表
            GdsBrandType gdsBrand = new GdsBrandType();
            gdsBrand.setGoodsTypeId(goodsTypeId);
            gdsBrand.setDeleted("1");
            gdsBrandTypeMapper.updateByPrimaryKeySelective(gdsBrand);
            //详细表
            GdsBrandTypeDetail gdsTypeDetail = new GdsBrandTypeDetail();
            gdsTypeDetail.setGoodsTypeId(goodsTypeId);
            gdsTypeDetail.setDeleted("1");
            gdsBrandTypeDtailMapper.updateByPrimaryKeySelective(gdsTypeDetail);
            map.put("resultCode", Constants.NORMAL);
        } else {
            map.put("resultCode", Constants.FAIL);
        }
        map.put("message", "");
        return map;
    }

    //上移动,下移动
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public JSONObject updateSort(String data) {
        List<GdsBrandType> list = JSONArray.parseArray(data, GdsBrandType.class);
        JSONObject json = new JSONObject();
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    gdsBrandTypeMapperExt.updateSort(list.get(i));
                }
            }
            json.put("resultCode", Constants.NORMAL);
        } catch (Exception e) {
            json.put("resultCode", Constants.FAIL);
        }
        return json;
    }
//
//
//    /***********************API**************************************/
//    /**
//     * 获取分类的第一层节点
//     * 默认ERP分类和电商分类都包含
//     * 如果传递参数获取电商分类或者Erp分类的第一层节点
//     *
//     * @param data type:规则编码
//     *             10.ERP分类，20.电商分类
//     * @return
//     */
//    public JSONObject getGdsTypeFirstFloor(String data) {
//        JSONObject map = new JSONObject();
//        try {
//            GdsType gdsType = new GdsType();
//            gdsType.setShopId(Constants.ORGID);
//            gdsType.setType(data);
//            List<GdsTypeExt> list = gdsTypeMapperExt.getGdsTypeFirstFloor(gdsType);
//            map.put("results", list);
//            map.put("resultCode", Constants.NORMAL);
//        } catch (Exception e) {
//            map.put("resultCode", Constants.FAIL);
//        }
//        return map;
//    }
//
//    /**
//     * 获取分类的第一层节点和第二层节点
//     * 默认ERP分类和电商分类都包含
//     * 如果传递参数获取电商分类或者Erp分类的第一层节点和第二层节点
//     *
//     * @param data type:规则编码
//     *             10.ERP分类，20.电商分类
//     * @return
//     */
//    public JSONObject getGdsTypeFirstAndSecondFloor(String data) {
//        JSONObject map = new JSONObject();
//        try {
//            GdsType gdsType = new GdsType();
//            gdsType.setShopId(Constants.ORGID);
//            gdsType.setType(data);
//
//            List<GdsTypeExt> allNods = new ArrayList<GdsTypeExt>();
//            // 获取cms维护的商品分类
//            List<CmsMasterExt> cmList = cmsMasterMapperExt.selectCmsMasterByItemCode("index_gds_sorts");
//            for (CmsMasterExt cmsMasterExt : cmList) {
//                String listJson = cmsMasterExt.getListJson();
//                JSONObject jsonObject = JSON.parseObject(listJson);
//                String gdsTypeId = jsonObject.getString("value");
//                String[] gdsTypeArray = gdsTypeId.split("_");
//                GdsTypeExt gdsTypeExt = gdsTypeMapperExt.getGdsTypeById(gdsTypeArray[0]);
//                if (allNods.isEmpty()) {
//                    allNods.add(gdsTypeExt);
//                } else {
//                    boolean addFlag = true;
//                    for (GdsTypeExt obj : allNods) {
//                        if (obj.getGoodsTypeId().equals(gdsTypeExt.getGoodsTypeId())) {
//                            addFlag = false;
//                            break;
//                        }
//                    }
//
//                    if (addFlag) {
//                        allNods.add(gdsTypeExt);
//                    }
//                }
//
////                // 如果为一级菜单的时候
////                if (gdsTypeArray.length == 1) {
////                    GdsTypeExt gdsTypeFirst = gdsTypeMapperExt.getGdsTypeById(gdsTypeId);
////                    //第一层的节点ID
////                    String goods_type_id =  gdsTypeFirst.getGoodsTypeId();
////                    gdsType.setGoodsTypeId(goods_type_id);
////                    List<GdsTypeExt> subList = gdsTypeMapperExt.getSubLevelFloorList(gdsType);
////
////                    gdsTypeFirst.setSecondTypeList(subList);
////                    allNods.add(gdsTypeFirst);
////
////                // 如果为二级菜单的时候
////                } else if (gdsTypeArray.length == 2) {
////                    GdsTypeExt gdsTypeFirst = gdsTypeMapperExt.getGdsTypeById(gdsTypeArray[0]);
////                    List<GdsTypeExt> subList = new ArrayList<GdsTypeExt>();
////                    subList.add(gdsTypeMapperExt.getGdsTypeById(gdsTypeArray[1]));
////                    gdsTypeFirst.setSecondTypeList(subList);
////                    allNods.add(gdsTypeFirst);
////                // 如果为三级菜单的时候
////                } else if (gdsTypeArray.length == 3) {
////                    // TODO 不处理第三季
////                }
////
////            }
//
////            List<GdsTypeExt> list = gdsTypeMapperExt.getGdsTypeFirstFloor(gdsType);
////            // 无法通过SQL获取该层数据。只能手动循环处理。
////            for (CmsMasterExt cmsMasterExt : cmList) {
////                String listJson = cmsMasterExt.getListJson();
////                JSONObject jsonObject = JSON.parseObject(listJson);
////                String gdsTypeId = jsonObject.getString("value");
////                for (GdsTypeExt gdsTypeExt : list) {
////                    if (gdsTypeExt.getGoodsTypeId().equals(gdsTypeId)) {
////                        allNods.add(gdsTypeExt);
////                        break;
////                    }
////                }
//            }
//            //根据第一层的节点ID获取第二层的节点
//            for (GdsTypeExt gdsTypeFirst : allNods) {
//                //第一层的节点ID
//                String goods_type_id = gdsTypeFirst.getGoodsTypeId();
//                gdsType.setGoodsTypeId(goods_type_id);
//                List<GdsTypeExt> subList = gdsTypeMapperExt.getSubLevelFloorList(gdsType);
//
//                gdsTypeFirst.setSecondTypeList(subList);
////                //把第一层节点的名字也拼接到第二层级的节点名称上边
////                for(GdsTypeExt subItem : subList){
////                    subItem.setGoodsTypeNameCn(gdsTypeFirst.getGoodsTypeNameCn().concat("_").concat(subItem.getGoodsTypeNameCn()));
////                    allNods.add(subItem);
////                }
//            }
////            allNods.addAll(firstCmList);
//
//            map.put("results", allNods);
//            map.put("resultCode", Constants.NORMAL);
//        } catch (Exception e) {
//            map.put("resultCode", Constants.FAIL);
//        }
//        return map;
//    }
//
//    /**
//     * 获取第三层数据以及第三层数据的图片
//     *
//     * @param data goods_type_id:商品分类ID
//     * @return
//     */
//    public JSONObject getGdsTypeSubFloor(String data) {
//        JSONObject map = new JSONObject();
//        try {
//            GdsType gdsType = new GdsType();
//            gdsType.setShopId(Constants.ORGID);
//            gdsType.setGoodsTypeId(data);
//            List<GdsTypeExt> subList = gdsTypeMapperExt.getSubLevelFloorList(gdsType);
//
//            map.put("results", subList);
//            map.put("resultCode", Constants.NORMAL);
//        } catch (Exception e) {
//            map.put("resultCode", Constants.FAIL);
//        }
//        return map;
//    }
//
//    /**
//     * 获取CMS配置的商品分类
//     * 默认ERP分类和电商分类都包含
//     * 如果传递参数获取电商分类或者Erp分类的商品分类
//     *
//     * @param data type:规则编码
//     *             10.ERP分类，20.电商分类
//     * @return
//     */
//    public JSONObject getGdsTypeFloor(String data) {
//        JSONObject map = new JSONObject();
//        try {
//            GdsType gdsType = new GdsType();
//            gdsType.setShopId(Constants.ORGID);
//            gdsType.setType(data);
//
//            List<GdsTypeExt> allNods = new ArrayList<GdsTypeExt>();
//            // 获取cms维护的商品分类
//            List<CmsMasterExt> cmList = cmsMasterMapperExt.selectCmsMasterByItemCode("index_gds_sorts");
//            for (CmsMasterExt cmsMasterExt : cmList) {
//                String listJson = cmsMasterExt.getListJson();
//                JSONObject jsonObject = JSON.parseObject(listJson);
//                String gdsTypeId = jsonObject.getString("value");
//                String[] gdsTypeArray = gdsTypeId.split("_");
//                GdsTypeExt gdsTypeExt = gdsTypeMapperExt.getGdsTypeById(gdsTypeArray[0]);
//                if (allNods.isEmpty()) {
//                    allNods.add(gdsTypeExt);
//                } else {
//                    boolean addFlag = true;
//                    for (GdsTypeExt obj : allNods) {
//                        if (obj.getGoodsTypeId().equals(gdsTypeExt.getGoodsTypeId())) {
//                            addFlag = false;
//                            break;
//                        }
//                    }
//
//                    if (addFlag) {
//                        allNods.add(gdsTypeExt);
//                    }
//                }
//            }
//            //根据第一层的节点ID获取第二层的节点
//            for (GdsTypeExt gdsTypeFirst : allNods) {
//                //第一层的节点ID
//                String goods_type_id = gdsTypeFirst.getGoodsTypeId();
//                gdsType.setGoodsTypeId(goods_type_id);
//                List<GdsTypeExt> subList = gdsTypeMapperExt.getSubLevelFloorList(gdsType);
//                // 获取第二层截点的子节点
//                for (GdsTypeExt threeGdsTypeExt : subList) {
//                    gdsType.setGoodsTypeId(threeGdsTypeExt.getGoodsTypeId());
//                    List<GdsTypeExt> threeSubList = gdsTypeMapperExt.getSubLevelFloorList(gdsType);
//                    threeGdsTypeExt.setSecondTypeList(threeSubList);
//                }
//
//                gdsTypeFirst.setSecondTypeList(subList);
////                //把第一层节点的名字也拼接到第二层级的节点名称上边
////                for(GdsTypeExt subItem : subList){
////                    subItem.setGoodsTypeNameCn(gdsTypeFirst.getGoodsTypeNameCn().concat("_").concat(subItem.getGoodsTypeNameCn()));
////                    allNods.add(subItem);
////                }
//            }
////            allNods.addAll(firstCmList);
//
//            map.put("results", allNods);
//            map.put("resultCode", Constants.NORMAL);
//        } catch (Exception e) {
//            map.put("resultCode", Constants.FAIL);
//        }
//        return map;
//    }
}
