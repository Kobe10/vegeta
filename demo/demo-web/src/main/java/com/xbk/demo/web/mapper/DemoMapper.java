//package com.xbk.demo.web.mapper;
//
//import com.xbk.demo.web.data.entity.Demo;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//import org.springframework.stereotype.Repository;
//import tk.mybatis.mapper.common.Mapper;
//
//import java.util.List;
//
//@Repository
//public interface DemoMapper extends Mapper<Demo> {
//
//    @Select("select demo_name FROM o_demo where demo_num in (#{numList})")
//    List<Demo> customizeSqlSelectByName2(@Param("numList") List<Integer> numList);
//}
