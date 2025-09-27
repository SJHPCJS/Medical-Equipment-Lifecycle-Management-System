package com.example.melms.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProcurementMapper {

    /* ---------- Suppliers ---------- */

    @Select("""
                SELECT supplier_id AS id,
                       supplier_name AS name,
                       COALESCE(contact, '') AS contact
                FROM tb_supplier
                ORDER BY supplier_id
            """)
    List<Map<String, Object>> listVendors();

    // 生成下一个 4 位 supplier_id（0001 起）
    @Select("SELECT LPAD(COALESCE(MAX(CAST(supplier_id AS UNSIGNED)),0)+1, 4, '0') FROM tb_supplier")
    String nextSupplierId();

    @Insert("INSERT INTO tb_supplier (supplier_id, supplier_name, contact) VALUES (#{id}, #{name}, #{contact})")
    void addVendor(@Param("id") String id, @Param("name") String name, @Param("contact") String contact);

    /* ---------- Equipment Types ---------- */

    @Select("""
                SELECT equipment_type_id AS id,
                       equipment_type_name AS name
                FROM tb_equipment_type
                ORDER BY equipment_type_id
            """)
    List<Map<String, Object>> listEquipmentTypes();

    /* ---------- Quotes ---------- */

    @Select("""
                SELECT supplier_id AS supplier_id,
                       equipment_type_id AS equipment_type_id,
                       price
                FROM tb_supplier_equipment_type
                ORDER BY supplier_id, equipment_type_id
            """)
    List<Map<String, Object>> listQuotes();

    @Select("""
                SELECT COUNT(*) FROM tb_supplier_equipment_type
                WHERE supplier_id = #{supplier_id} AND equipment_type_id = #{equipment_type_id}
            """)
    Integer countQuote(@Param("supplier_id") String supplierId, @Param("equipment_type_id") String typeId);

    @Insert("""
                INSERT INTO tb_supplier_equipment_type (supplier_id, equipment_type_id, price)
                VALUES (#{supplier_id}, #{equipment_type_id}, #{price})
            """)
    void addQuote(@Param("supplier_id") String supplierId,
                  @Param("equipment_type_id") String typeId,
                  @Param("price") int price);

    @Update("""
                UPDATE tb_supplier_equipment_type
                SET price = #{price}
                WHERE supplier_id = #{supplier_id} AND equipment_type_id = #{equipment_type_id}
            """)
    void updateQuote(@Param("supplier_id") String supplierId,
                     @Param("equipment_type_id") String typeId,
                     @Param("price") int price);

    @Delete("""
                DELETE FROM tb_supplier_equipment_type
                WHERE supplier_id = #{supplier_id} AND equipment_type_id = #{equipment_type_id}
            """)
    void deleteQuote(@Param("supplier_id") String supplierId, @Param("equipment_type_id") String typeId);

    /* ---------- Orders ---------- */

    @Select("""
                SELECT procure_id      AS procure_id,
                       equipment_type_id AS equipment_type_id,
                       count           AS count,
                       supplier_id     AS supplier_id,
                       status          AS status
                FROM tb_procure_order
                ORDER BY procure_id DESC
            """)
    List<Map<String, Object>> listOrders();

    @Update("UPDATE tb_procure_order SET status = #{status} WHERE procure_id = #{procure_id}")
    void updateOrderStatus(@Param("procure_id") int procureId, @Param("status") String status);

    @Update("""
                UPDATE tb_procure_order
                SET supplier_id = #{supplier_id}, count = #{count}
                WHERE procure_id = #{procure_id}
            """)
    void assignOrder(@Param("procure_id") int procureId,
                     @Param("supplier_id") String supplierId,
                     @Param("count") int count);
}
